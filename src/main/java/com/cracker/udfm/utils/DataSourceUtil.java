package com.cracker.udfm.utils;


import com.alibaba.druid.pool.DruidDataSource;
import com.cracker.udfm.core.DbServer;
import com.cracker.udfm.datasource.pooled.PooledDataSource;
import com.cracker.udfm.utils.io.Resources;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * 类: 数据库连接组件.
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class DataSourceUtil {

    private static final String DEFAULT_DATA_SOURCE_FILE = "datasource.properties";

    /**
     * 创建Druid(德鲁伊)数据库连接池
     * @param server DbServer
     * @return DataSource
     * @throws IOException IOException
     */
    public static DataSource createDruidDataSource(DbServer server) 
            throws IOException {
        
        DruidDataSource dataSource;
        Properties props = Resources.getResourceAsProperties(DEFAULT_DATA_SOURCE_FILE);
        
        dataSource = new DruidDataSource();
        dataSource.setUrl(server.getUrl());
        dataSource.setUsername(server.getUser());
        dataSource.setPassword(server.getPass());
        dataSource.setDriverClassName(server.getDriver());

        //初始化连接数量
        dataSource.setInitialSize(Integer.parseInt(props.getProperty("datasource.initSize","5")));
        //最大并发连接数
        dataSource.setMaxActive(Integer.parseInt(props.getProperty("datasource.maxActive","200")));
        //最小空闲连接数
        dataSource.setMinIdle(Integer.parseInt(props.getProperty("datasource.minIdle","3")));
        //配置获取连接等待超时的时间
        dataSource.setMaxWait(Integer.parseInt(props.getProperty("datasource.maxWait","6000")));
        //超过时间限制是否回收
        dataSource.setRemoveAbandoned(true);
        //超过时间限制多长
        dataSource.setRemoveAbandonedTimeout(Integer.parseInt(props.getProperty("datasource.removeAbandonedTimeout","180")));
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(props.getProperty("datasource.timeBetweenEvictionRunsMillis","60000")));
        //配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(props.getProperty("datasource.minEvictableIdleTimeMillis","300000")));
        //用来检测连接是否有效的sql，要求是一个查询语句
        dataSource.setValidationQuery("SELECT 1");
        //申请连接的时候检测
        dataSource.setTestWhileIdle(true);
        //申请连接时执行validationQuery检测连接是否有效，配置为true会降低性能
        dataSource.setTestOnBorrow(false);
        //归还连接时执行validationQuery检测连接是否有效，配置为true会降低性能
        dataSource.setTestOnReturn(false);
        //打开PSCache，并且指定每个连接上PSCache的大小
        dataSource.setPoolPreparedStatements(true);
        //最大缓存语句
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(props.getProperty("datasource.maxPoolPreparedStatementPerConnectionSize","200")));

        return dataSource;
    }


    public static void getPooledDataSource(String resource) {

    }

}
