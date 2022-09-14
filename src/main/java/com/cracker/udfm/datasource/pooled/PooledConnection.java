package com.cracker.udfm.datasource.pooled;

import com.cracker.udfm.utils.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 类: 连接代理
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] I_FACES = new Class<?>[] {Connection.class};

    private int hashCode = 0;

    /**
     * 有连接池的数据源
     */
    private PooledDataSource dataSource;

    /**
     * 真实连接
     */
    private Connection realConnection;
    /**
     * 代理连接
     */
    private Connection proxyConnection;
    /**
     * 连接超时时间(ms)
     * 测试可删，后期改为：connectionTimeOut
     */
    private long checkoutTimestamp;
    /**
     * 创建连接时间(ms)
     */
    private long createdTimestamp;
    /**
     * 最近一次连接时间(ms)
     */
    private long lastUsedTimestamp;
    /**
     * 连接类型编码
     */
    private int connectionTypeCode;
    /**
     * 是否需要验证连接
     */
    private boolean valid;

    /**
     * 简单的PooledConnection构造器，仅使用Connection和PooledDataSource
     * @param connection - the connection that is to be presented as a pooled connection
     * @param dataSource - the dataSource that the connection is from
     */
    public PooledConnection(Connection connection, PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), I_FACES, this);
    }

    /**
     * invalidate the realConnection
     */
    public void invalidate() {
        this.valid = false;
    }

    /**
     * 验证该realConnection是否可用
     * @return True if the connection is usable
     */
    public boolean isValid() {
        return this.valid && this.realConnection != null && this.dataSource.pingConnection(this);
    }

    public Connection getRealConnection() {
        return this.realConnection;
    }

    public Connection getProxyConnection() {
        return this.proxyConnection;
    }

    public int getRealHashCode() {
        return this.realConnection == null ? 0 : realConnection.hashCode();
    }

    public int getConnectionTypeCode() {
        return this.connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    /**
     * Getter for the age of the connection
     * @return return the age
     */
    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }

    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object primitive) {
        if (primitive instanceof PooledConnection) {
            return this.realConnection.hashCode() == ((PooledConnection) primitive).realConnection.hashCode();
        } else if (primitive instanceof Connection) {
            return this.hashCode == primitive.hashCode();
        }
        return false;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
            this.dataSource.pushConnection(this);
            return null;
        }

        try {
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }
            return method.invoke(realConnection, args);
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }


    }

    private void checkConnection() throws SQLException {
        if (!this.valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }
    }
}