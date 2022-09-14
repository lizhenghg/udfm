package com.cracker.udfm.core;

/**
 * 数据存储类型
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-04
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public enum DataFieldType {
    /**
     * 基本数据类型和常见引用数据类型
     * 基本数据类型：short、int、long、char、byte、boolean、float、double
     * 引用数据类型：String、ObjectId、Number、Date、Pattern、byte[]、Binary、UUID
     */
    ATOM,
    /**
     * 指定的类
     */
    CLASS,
    /**
     * 集合
     */
    COLLECTION,
    /**
     * key-value键值对
     */
    MAP,
    /**
     * 数组
     */
    ARRAY
}
