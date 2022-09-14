package com.cracker.udfm.annotations;

import com.cracker.udfm.common.DbType;

import java.lang.annotation.*;

/**
 * 注解：数据库存储字段.
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-05
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * name, 字段名
     * @return String
     */
    public String name();

    /**
     * type, 字段类型
     * @return DbType
     */
    public DbType type();

    /**
     * 是否自增
     * @return boolean
     */
    boolean isAutoIncrease() default false;

    /**
     * 是否允许值为null
     * @return boolean
     */
    boolean isStoreNull() default true;
}
