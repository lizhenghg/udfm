package com.cracker.udfm.core;

import com.cracker.udfm.common.AbstractDatabase;

/**
 * 类: 结果集
 * 一个类型为T的数据对象集的操作接口，支持批量增删改查，对象成员同存储字段的映射通过annotation由反射机制完成,额外传递的参数通过列表传入,参数只支持基本数据类型和byte[],
 * 数据对象集支持使用其他的对象集作为本对象集的缓存。对于每个操作可以定义一个command
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-06
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class DataSet {

    /**
     * 扩展参数，所有支持象memcached一样的拥有失效时间
     */
    public static final String EXP_EXPIRE = "exp";
    /**
     * 结果集名称
     */
    public String name;
    /**
     * 是否对结果集进行缓存处理
     */
    public boolean isCache;

    public AbstractDatabase db = null;



}
