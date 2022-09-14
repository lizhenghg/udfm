package com.cracker.udfm.datasource.pooled;

import com.cracker.udfm.datasource.unpooled.UnPooledDataSource;
import com.cracker.udfm.utils.Assert;
import com.cracker.udfm.utils.Logger;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;


/**
 * 类: 带连接池的数据源
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class PooledDataSource implements DataSource {

    /**
     * 连接池状态
     */
    private final PoolState state = new PoolState(this);

    private final UnPooledDataSource dataSource;

    /**
     * 在任意时间可以存在的活动（也就是正在使用）连接数量，默认值：10.
     */
    protected int poolMaximumActiveConnections = 10;
    /**
     * 任意时间可能存在的空闲连接数
     */
    protected int poolMaximumIdleConnections = 5;
    /**
     * 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）.
     */
    protected int poolMaximumCheckoutTime = 20000;
    /**
     * 这是一个底层设置，如果获取连接花费的相当长的时间，它会给连接池打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直安静的失败），默认值：20000 毫秒（即 20 秒）.
     */
    protected int poolTimeToWait = 20000;
    /**
     * 发送到数据库的侦测查询，用来检验连接是否处在正常工作秩序中并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动失败时带有一个恰当的错误消息.
     */
    protected String poolPingQuery = "NO PING QUERY SET";
    /**
     * 是否启用侦测查询。若开启，也必须使用一个可执行的 SQL 语句设置 poolPingQuery 属性（最好是一个非常快的 SQL），默认值：false.
     */
    protected boolean poolPingEnabled = false;
    /**
     * 配置 poolPingQuery的使用频度。这可以被设置成匹配具体的数据库连接超时时间，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。
     */
    protected int poolPingConnectionsNotUsedFor = 0;

    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnPooledDataSource();
    }

    public PooledDataSource(UnPooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PooledDataSource(final String driver, final String dataSourceUrl, final String username, final String password) {
        dataSource = new UnPooledDataSource(driver, dataSourceUrl, username, password);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getDataSourceUrl(), dataSource.getUserName(), dataSource.getPassword());
    }

    public PooledDataSource(final String driver, final String dataSourceUrl, final Properties driverProperties)
    {
        dataSource = new UnPooledDataSource(driver, dataSourceUrl, driverProperties);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getDataSourceUrl(), dataSource.getUserName(), dataSource.getPassword());
    }

    public PooledDataSource(final ClassLoader driverClassLoader, final String driver, final String dataSourceUrl, final String username, final String password)
    {
        dataSource = new UnPooledDataSource(driverClassLoader, driver, dataSourceUrl, username, password);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getDataSourceUrl(), dataSource.getUserName(), dataSource.getPassword());
    }

    public PooledDataSource(final ClassLoader driverClassLoader, final String driver, final String dataSourceUrl, final Properties driverProperties)
    {
        dataSource = new UnPooledDataSource(driverClassLoader, driver, dataSourceUrl, driverProperties);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getDataSourceUrl(), dataSource.getUserName(), dataSource.getPassword());
    }

    private int assembleConnectionTypeCode(String dataSourceUrl, String userName, String password) {
        return ("" + dataSourceUrl + userName + password).hashCode();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(this.dataSource.getUserName(), this.dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    /**
     * 从连接池中返回一个可用的PooledConnection对象
     * @param userName 用户名
     * @param password 用户密码
     * @return PooledConnection
     * @throws SQLException sql Exception
     */
    private PooledConnection popConnection(String userName, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection conn = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while (conn == null) {
            synchronized (state) {
                // 1.先看是否有空闲(idle)状态下的PooledConnection对象,
                // 如果有，就直接返回一个可用的PooledConnection对象；否则进行第2步。
                if (!this.state.idleConnections.isEmpty()) {
                    conn = this.state.idleConnections.poll();
                    if (Logger.isDebugEnabled()) {
                        Logger.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
                    }
                } else {
                    // 2.查看活动状态的PooledConnection沲activeConnection是否已满;
                    // 如果没有满，则创建一个新的PooledConnection对象，然后放到activeConnections池中，
                    // 然后返回PooledConnection对象;否则进入第三步
                    if (this.state.activeConnections.size() < poolMaximumActiveConnections) {
                        conn = new PooledConnection(dataSource.getConnection(), this);
                        if (Logger.isDebugEnabled()) {
                            Logger.debug("Created connection " + conn.getRealHashCode() + ".");
                        }
                    } else {
                        // 3.看最先进入activeConnections池中的PooledConnection对象是否已经过期：
                        // 如果已经过期，从activeConnections池中移除此对象，
                        // 然后创建一个新的PooledConnection对象，
                        // 添加到activeConnections中，然后将此对象返回；否则进行第4步。
                        PooledConnection oldestActiveConnection = this.state.activeConnections.poll();
                        Assert.checkArgument(oldestActiveConnection != null, "NullPoint Exception For Connection");
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            // 3.1累计已经过期的连接数
                            this.state.claimedOverdueConnectionCount++;
                            // 3.2累计超时和过期的连接数
                            this.state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            // 3.3累计连接超时数
                            this.state.accumulatedCheckoutTime += longestCheckoutTime;
                            // 3.4判断过期连接是否是自动提交，撤消对当前事务中所做的所有更改和任何数据库当前持有锁的释放
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                oldestActiveConnection.getRealConnection().rollback();
                            }
                            // 3.5创建一个新的PooledConnection对象
                            // 测试可删，存有异议？
                            conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            // 3.6标记不可用
                            oldestActiveConnection.invalidate();
                            if (Logger.isDebugEnabled()) {
                                Logger.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
                            }
                        } else {
                            // 必须等待
                            try {
                                if (!countedWait) {
                                    this.state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                if (Logger.isDebugEnabled()) {
                                    Logger.debug("waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                }
                                long wt = System.currentTimeMillis();
                                this.state.wait(poolTimeToWait);
                                this.state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
                if (conn != null) {
                    // 判断连接是否可用
                    if (conn.isValid()) {
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        // 设置连接类型hash值，code=(url+username+password).hashCode()
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getDataSourceUrl(), userName, password));
                        // 设置检测超时时间，默认为当前系统时间
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        // 设置最后一次使用连接的时间，默认获取系统的最近一次时间
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        // 添加到activeConnections中，然后将此对象返回
                        this.state.activeConnections.offer(conn);
                        // 累计请求数
                        this.state.requestCount++;
                        // 累计请求时间，accumnlatedRequestTime = accumnlatedRequestTime + (System.currentTimeMillis() - t)
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        if (Logger.isDebugEnabled()) {
                            Logger.debug("A bad connection (" + conn.getRealHashCode()+ ") was returned from the pool, getting another connection.");
                        }
                        // 累计不可用的连接数
                        this.state.badConnectionCount++;
                        // 累计当前不可用的连接数
                        localBadConnectionCount++;
                        conn = null;
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            if (Logger.isDebugEnabled()) {
                                Logger.debug("PooledDataSource: Could not get a good connection to the database.");
                            }
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        if (conn == null) {
            if (Logger.isDebugEnabled()) {
                Logger.debug("PooledDataSource: Unknown server error condition. The connection pool returned a null connection.");
            }
            throw new SQLException("PooledDataSource: Unknown server error condition. The connection pool returned a null connection.");
        }
        return conn;
    }

    /**
     * Method to check to see if a connection is still usable
     * @param conn - the connection to check
     * @return - True if the connection is still usable
     */
    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;
        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException throwables) {
            if (Logger.isDebugEnabled()) {
                Logger.debug("Connection " + conn.getRealHashCode() + " is BAD: " + throwables.getMessage());
            }
            result = false;
        }

        if (result) {
            if (poolPingEnabled) {
                if (poolPingConnectionsNotUsedFor >= 0
                        && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    if (Logger.isDebugEnabled()) {
                        Logger.debug("Testing connection " + conn.getRealHashCode() + " ...");
                    }
                    try {
                        Connection realConn = conn.getRealConnection();
                        Statement statement = realConn.createStatement();
                        ResultSet rs = statement.executeQuery(poolPingQuery);
                        rs.close();
                        statement.close();
                        if (!realConn.getAutoCommit()) {
                            realConn.rollback();
                        }
                        result = true;
                        if (Logger.isDebugEnabled()) {
                            Logger.debug("Connection " + conn.getRealHashCode() + " id GOOD!");
                        }
                    } catch (Exception e) {
                        Logger.warn("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            conn.getRealConnection().close();
                        } catch (Exception ignore) {
                        }
                        result = false;
                        if (Logger.isDebugEnabled()) {
                            Logger.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                        }
                    }

                }
            }
        }
        return result;
    }

    protected void pushConnection(PooledConnection conn) throws SQLException {

        synchronized (this.state) {
            if (conn.isValid()) {
                // getConnectionTypeCode()这里类似于token验证，确保该connection是safe的
                if (this.state.idleConnections.size() < poolMaximumIdleConnections
                        && conn.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    this.state.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
                    this.state.idleConnections.offer(newConn);
                    newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
                    newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
                    conn.invalidate();
                    if (Logger.isDebugEnabled()) {
                        Logger.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
                    }
                    this.state.notifyAll();
                }
            } else {
                if (Logger.isDebugEnabled()) {
                    // failed to return to the pool, equals to ==> attempted to return to the pool
                    Logger.debug("A bad connection (" + conn.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                    this.state.badConnectionCount++;
                }
            }
        }
    }
}
