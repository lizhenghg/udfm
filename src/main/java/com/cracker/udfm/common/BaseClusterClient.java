package com.cracker.udfm.common;

import com.cracker.udfm.core.DbServer;

import java.util.HashMap;
import java.util.Map;

/**
 * 类: 集群客户端
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-02
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class BaseClusterClient {

    /**
     * 集群服务器名称
     */
    private String name;
    /**
     * 数据源类型，1表示关系型数据库
     */
    protected String type;
    /**
     * 集群服务器缓存表
     */
    protected Map<Integer, DbServer> servers = new HashMap<>();

    /**
     * 检测服务器启动情况
     * @return 0 means OK, 1 means fail
     */
    public abstract int toBeReady();

    /**
     * 关闭连接
     * @return 0 means OK
     */
    public abstract int close();

    private String clientClass;

    /**
     * 添加集群服务器
     * @param dbServer 数据库对应服务器类
     * @return 0 means OK, 1 means fail
     */
    public int addServer(DbServer dbServer) {
        if (servers.get(dbServer.getSid()) == null) {
            servers.put(dbServer.getSid(), dbServer);
            return 0;
        }
        return 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientClass() {
        return clientClass;
    }

    public void setClientClass(String clientClass) {
        this.clientClass = clientClass;
    }

    @Override
    public String toString() {
        return String.format("BaseClusterClient[name = %s, type = %s, clientClass = %s]"
                ,getName(), getType(), getClientClass());
    }
}
