package com.cracker.udfm.common;

/**
 * 数据存储类型，枚举
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-06
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public enum  DbType {
    AUTO,
    /****** with schema,like mySql need this ******/
    INT,
    TINYINT,
    SMALLINT,
    BIGINT,
    CHAR,
    VARCHAR,
    FLOAT,
    DOUBLE,

    /****** 只支持PostgreSQL ******/
    NUMBERIC,
    SMALLSERIAL,
    SERIAL,
    BIGSERIAL,


    TEXT,
    DATETIME,
    BLOB,
    CLOB,
    EXPAND,
    CLASS,
    ARRAY,
    COLLECTION
}
