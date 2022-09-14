package com.cracker.udfm.datasource.unpooled;

import com.cracker.udfm.utils.io.Resources;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.Map;

/**
 * 类: 不带连接池的数据源
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class UnPooledDataSource implements DataSource {

    /**
     * 连接驱动类加载器
     */
    private ClassLoader driverClassLoader;

    /**
     * 连接驱动属性表
     */
    private Properties driverProperties;

    /**
     * 连接驱动程序。注册到内存中的驱动，以供后续使用
     */
    private static final Map<String, Driver> REGISTER_DRIVERS = new ConcurrentHashMap<>();

    /**
     * 数据库连接驱动
     */
    private String driver;

    /**
     * 数据库连接url
     */
    private String dataSourceUrl;

    /**
     * 数据库连接用户名
     */
    private String userName;

    /**
     * 数据库连接密码
     */
    private String password;

    /**
     * 设置是否自动提交
     */
    private Boolean autoCommit;

    /**
     * 设置事务的隔离级别
     * 1.Read Uncommitted 指定语句可以读取已由其他事务修改但尚未提交的行.最低等级的事务隔离，仅仅保证了读取过程中不会读取到非法数据.</br>
     * 2.Read Committed：大多数主流数据库的默认事务等级，保证了一个事务不会读到另一个并行事务已修改但未提交的数据，避免了“脏读取”</br>
     * 3.Repeatable Read：保证了一个事务不会修改已经由另一个事务读取但未提交（回滚）的数据。避免了“脏读取”和“不可重复读取”的情况，但是带来了更多的性能损失</br>
     * 4.Serializable：最高等级的事务隔离，上面3种不确定情况都将被规避。这个级别将模拟事务的串行执行。</br>
     */
    private Integer defaultTransactionIsolationLevel;

    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            REGISTER_DRIVERS.put(driver.getClass().getName(), driver);
        }
    }

    public UnPooledDataSource() {}

    public UnPooledDataSource(final String driver, final String dataSourceUrl, final Properties driverProperties) {
        this(null, driver, dataSourceUrl, null, null, driverProperties);
    }

    public UnPooledDataSource(final String driver, final String dataSourceUrl, final String userName, final String password) {
        this(null, driver, dataSourceUrl, userName, password, null);
    }

    public UnPooledDataSource(final ClassLoader driverClassLoader, final String driver, final String dataSourceUrl, final Properties driverProperties) {
        this(driverClassLoader, driver, dataSourceUrl, null, null, driverProperties);
    }

    public UnPooledDataSource(final ClassLoader driverClassLoader, final String driver, final String dataSourceUrl, final String userName, final String password) {
        this(driverClassLoader, driver, dataSourceUrl, userName, password, null);
    }

    public UnPooledDataSource(final ClassLoader driverClassLoader, final String driver, final String dataSourceUrl,
                              final String userName, final String password, final Properties driverProperties) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.dataSourceUrl = dataSourceUrl;
        this.userName = userName;
        this.password = password;
        this.driverProperties = driverProperties;
    }


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(userName, password);
    }

    @Override
    public Connection getConnection(String userName, String password) throws SQLException {
        return doGetConnection(userName, password);
    }

    /**
     * 获取数据库连接对象
     * @param userName 用户名
     * @param password 用户密码
     * @return 数据库连接实例
     * @throws SQLException sql Exception
     */
    private Connection doGetConnection(String userName, String password) throws SQLException {
        Properties props = new Properties();
        if (driverProperties != null) {
            props.putAll(driverProperties);
        }
        if (userName != null) {
            props.setProperty("user", userName);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return doGetConnection(props);
    }

    /**
     * 获取数据库连接对象
     * @param properties Properties file
     * @return 数据库连接实例
     * @throws SQLException sql Exception
     */
    private Connection doGetConnection(Properties properties) throws SQLException {
        initializeDriver();
        Connection connection = DriverManager.getConnection(dataSourceUrl, properties);
        configureConnection(connection);
        return connection;
    }


    private synchronized void initializeDriver() throws SQLException {
        if (!REGISTER_DRIVERS.containsKey(driver)) {
            Class<?> driverType;
            try {
                if (driverClassLoader != null) {
                    driverType = Class.forName(driver, true, driverClassLoader);
                } else {
                    driverType = Resources.classForName(driver);
                }
                Driver driverInstance = (Driver) driverType.newInstance();
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                REGISTER_DRIVERS.put(driver, driverInstance);
            } catch (Exception ex) {
                throw new SQLException("Error setting driver on UnPooledDataSource. Cause: " + ex);
            }
        }
    }


    private void configureConnection(Connection conn) throws SQLException {
        if (autoCommit != null && autoCommit != conn.getAutoCommit()) {
            conn.setAutoCommit(autoCommit);
        }
        if (defaultTransactionIsolationLevel != null) {
            conn.setTransactionIsolation(defaultTransactionIsolationLevel);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    /**
     * 连接驱动代理
     */
    private static class DriverProxy implements Driver {

        private final Driver driver;

        DriverProxy(Driver d) {
            this.driver = d;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return this.driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return this.driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return this.driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }
}
