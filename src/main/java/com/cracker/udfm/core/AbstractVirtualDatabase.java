package com.cracker.udfm.core;

import com.cracker.udfm.common.AbstractDatabase;

/**
 * 虚拟数据库存储
 *
 * 这是一个抽象的类，它提供了对数据逻辑上的一种高度抽象，单纯是从逻辑上的一种概念。
 * 实际其内部实现而言，其实是对物理存储上的一种聚合关系，是物理数据存储的集合概念。
 * 目前不支持这种存储方式。待后续有时间再实现此种方式的存储
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-04
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class AbstractVirtualDatabase extends AbstractDatabase {
}
