package com.cracker.udfm.datasource.pooled;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 类: 连接池状态
 * 
 * 1.连接池状态分为：空闲状态(Idle)和活动状态(Active)两种状态.
 * 2.这两种状态的PooledConnection对象分别被存储到PoolState容器内的idleConnections
 * 和 activeConnections 两个List集合中 
 * 
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class PoolState {
    /**
     * 自定义的数据源连接池
     */
    protected PooledDataSource dataSource;
    /**
     * 空闲连接沲列表.
     *
     * 空闲(idle)状态PooledConnection对象被放置到此集合中，表示当前闲置的没有被使用的PooledConnection集合</br>
     * 调用PooledDataSource的getConnection()方法时，会优先从此集合中取PooledConnection对象,</br>
     * 当用完一个java.sql.Connection对象时，会将其包裹成PooledConnection对象放到此集合中。</br>
     */
    protected final LinkedList<PooledConnection> idleConnections = new LinkedList<>();

    /**
     * 活动连接池列表.
     *
     * 活动(active)状态的PooledConnection对象被放置到名为activeConnections的ArrayList中，</br>
     * 表示当前正在被使用的PooledConnection集合，调用PooledDataSource的getConnection()方法时，</br>
     * 会优先从idleConnections集合中取PooledConnection对象,如果没有，则看此集合是否已满，</br>
     * 如果未满，PooledDataSource会创建出一个PooledConnection，添加到此集合中，并返回。</br>
     */
    protected final LinkedList<PooledConnection> activeConnections = new LinkedList<>();

    /**
     * 当前请求数
     */
    protected long requestCount = 0;

    /**
     * 累计请求时间
     */
    protected long accumulatedRequestTime = 0;

    /**
     * 累计检测超时时间.
     */
    protected long accumulatedCheckoutTime = 0;

    /**
     * 即将过期的连接总数
     */
    protected long claimedOverdueConnectionCount = 0;
    /**
     * 累计检测超时的连接数.
     */
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0;
    /**
     * 累计等待时间.
     */
    protected long accumulatedWaitTime = 0;
    /**
     * 统计等待连接数.
     */
    protected long hadToWaitCount = 0;
    /**
     * 累计连接失败数.
     */
    protected long badConnectionCount = 0;

    public PoolState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public synchronized long getRequestCount() {
        return requestCount;
    }

    /**
     * 获取平均请求时间
     * @return 平均请求时间
     */
    public synchronized long getAverageRequestTime() {
        return this.requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
    }

    /**
     * 获取平均等待时间.
     */
    public synchronized long getAverageWaitTime() {
        return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
    }

    /**
     * 获取等待数
     * @return 获取等待数
     */
    public synchronized long getHadToWaitCount() {
        return hadToWaitCount;
    }

    /**
     * 获取无用的连接数
     * @return 无用的连接数
     */
    public synchronized long getBadConnectionCount() {
        return badConnectionCount;
    }

    /**
     * 获取即将过期的连接总数.
     */
    public synchronized long getClaimedOverdueConnectionCount() {
        return claimedOverdueConnectionCount;
    }

    /**
     * 获取平均即将过期的连接总数.
     */
    public synchronized long getAverageOverdueCheckoutTime() {
        return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
    }

    /**
     * 获取平均超时时间.
     */
    public synchronized long getAverageCheckoutTime() {
        return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
    }

    /**
     * 获取空闲连接总数.
     */
    public synchronized int getIdleConnectionCount() {
        return idleConnections.size();
    }

    /**
     * 获取活动连接总数.
     */
    public synchronized int getActiveConnectionCount() {
        return activeConnections.size();
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n=======================数据源配置==========================");
        builder.append("\n jdbcDriver                     ").append(dataSource.getDriver());
        builder.append("\n jdbcUrl                        ").append(dataSource.getJdbcUrl());
        builder.append("\n jdbcUsername                   ").append(dataSource.getUsername());
        builder.append("\n jdbcPassword                   ").append((dataSource.getPassword() == null ? "NULL" : "************"));
        builder.append("\n poolMaxActiveConnections       ").append(dataSource.poolMaximumActiveConnections);
        builder.append("\n poolMaxIdleConnections         ").append(dataSource.poolMaximumIdleConnections);
        builder.append("\n poolMaxCheckoutTime            ").append(dataSource.poolMaximumCheckoutTime);
        builder.append("\n poolTimeToWait                 ").append(dataSource.poolTimeToWait);
        builder.append("\n poolPingEnabled                ").append(dataSource.poolPingEnabled);
        builder.append("\n poolPingQuery                  ").append(dataSource.poolPingQuery);
        builder.append("\n poolPingConnectionsNotUsedFor  ").append(dataSource.poolPingConnectionsNotUsedFor);
        builder.append("\n --------------------------连接状态------------------------------");
        builder.append("\n activeConnections              ").append(getActiveConnectionCount());
        builder.append("\n idleConnections                ").append(getIdleConnectionCount());
        builder.append("\n requestCount                   ").append(getRequestCount());
        builder.append("\n averageRequestTime             ").append(getAverageRequestTime());
        builder.append("\n averageCheckoutTime            ").append(getAverageCheckoutTime());
        builder.append("\n claimedOverdue                 ").append(getClaimedOverdueConnectionCount());
        builder.append("\n averageOverdueCheckoutTime     ").append(getAverageOverdueCheckoutTime());
        builder.append("\n hadToWait                      ").append(getHadToWaitCount());
        builder.append("\n averageWaitTime                ").append(getAverageWaitTime());
        builder.append("\n badConnectionCount             ").append(getBadConnectionCount());
        builder.append("\n===============================================================");

        return builder.toString();
    }
}
