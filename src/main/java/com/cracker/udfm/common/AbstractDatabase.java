package com.cracker.udfm.common;
/**
 * 数据库最顶级抽象父类
 *
 * 这是一个抽象类，提供了对数据的基本操作(CURD),它是一个物理数据存储的高度抽象类
 * 所有对数据存储的相关操作必须继承自它来完成对数据的基本操作
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-02
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class AbstractDatabase {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   

}
