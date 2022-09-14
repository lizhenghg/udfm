package com.cracker.udfm.core;


/**
 * 类: 数据库对应服务器类
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class DbServer {

    private int sid;
    /**
     * NoSql必选属性
     */
    private String name;
    private String host;
    private int port;

    /**
     * 公共属性
     */
    private int timeout;
    private int policy;
    private int weight;
    private String schema;

    /**
     * mysql属性
     */
    private String url;
    private String driver;
    private String user;
    private String pass;


    private int maxSize;
    private int minSize;
    private int idleConnection;
    private boolean fsync = false;


    /**
     * 静态内部类，关系型数据库
     */
    public static class MysqlServer {
        private final String url;
        private final String driver;
        private final String user;
        private final String pass;

        private int sid;
        private int timeout = 10000;
        private int maxSize = 5;
        private int minSize = 5;
        private int idleConnection = 10;

        public MysqlServer(String url, String driver, String user, String pass) {
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.pass = pass;
        }

        public MysqlServer withSid(int sid) {
            this.sid = sid;
            return this;
        }

        public MysqlServer withTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public MysqlServer withMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public MysqlServer withMinSize(int minSize) {
            this.minSize = minSize;
            return this;
        }

        public MysqlServer withIdleConnection(int idleConnection) {
            this.idleConnection = idleConnection;
            return this;
        }

        public DbServer build() {
            return new DbServer(this);
        }

    }

    /**
     * 非关系类数据库
     */
    public static class NosqlServer {

        public static final int READ_ONLY = 1;
        public static final int WRITE_ONLY = 2;
        public static final int READ_WRITE = 3;

        // Required parameters
        private String name;
        private final String host;
        private final int port;

        // Optional parameters.initialized to default value
        private int sid;
        // 读写策略，1，只读，2，只写，3，读写
        private int policy;
        private String user;
        private String pass;
        private String schema;
        private int timeout = 1000;
        private int weight = 0;
        // 数据一致性策略
        private boolean fsync = false;

        public NosqlServer(final String host, final int port) {
            this.host = host;
            this.port = port;
            this.withPolicy(READ_ONLY);
        }

        public NosqlServer(final String host, final int port, final String name) {
            this.host = host;
            this.port = port;
            this.name = name;
            this.withPolicy(READ_ONLY);
        }

        public NosqlServer withSid(final int sid) {
            this.sid = sid;
            return this;
        }

        public NosqlServer withPolicy(final int policy) {
            this.policy = policy;
            return this;
        }

        public NosqlServer withUser(final String user) {
            this.user = user;
            return this;
        }

        public NosqlServer withPass(final String pass) {
            this.pass = pass;
            return this;
        }

        public NosqlServer withSchema(final String schema) {
            this.schema = schema;
            return this;
        }

        public NosqlServer withTimeout(final int timeout) {
            this.timeout = timeout;
            return this;
        }

        public NosqlServer withWeight(final int weight) {
            this.weight = weight;
            return this;
        }

        public NosqlServer withFsync(final boolean fsync) {
            this.fsync = fsync;
            return this;
        }

        public DbServer build() {
            return new DbServer(this);
        }
    }

    public DbServer(MysqlServer builder) {
        this.sid = builder.sid;

        this.url = builder.url;
        this.driver = builder.driver;
        this.user = builder.user;
        this.pass = builder.pass;

        this.timeout = builder.timeout;
        this.maxSize = builder.maxSize;
        this.minSize = builder.minSize;
        this.idleConnection = builder.idleConnection;

    }

    public DbServer(NosqlServer builder) {

        this.name = builder.name;
        this.host = builder.host;
        this.port = builder.port;

        this.sid  	 = builder.sid;
        this.policy  = builder.policy;
        this.user 	 = builder.user;
        this.schema	 = builder.schema;
        this.pass 	 = builder.pass;
        this.timeout = builder.timeout;
        this.weight  = builder.weight;
        this.fsync   = builder.fsync;

    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPolicy() {
        return policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getIdleConnection() {
        return idleConnection;
    }

    public void setIdleConnection(int idleConnection) {
        this.idleConnection = idleConnection;
    }

    public boolean isFsync() {
        return fsync;
    }

    public void setFsync(boolean fsync) {
        this.fsync = fsync;
    }
}
