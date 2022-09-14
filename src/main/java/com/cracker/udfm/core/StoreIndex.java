package com.cracker.udfm.core;

import java.util.LinkedHashSet;

import java.util.Set;

/**
 * 类: 存储索引
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-16
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class StoreIndex {

    /**
     * 索引名称
     */
    private String name;
    /**
     * 索引字段集合
     */
    private Set<IndexField> indexFields = new LinkedHashSet<>();

    /**
     * 是否为主键索引
     */
    private boolean isPk = false;

    /**
     * 是否为唯一索引
     */
    private boolean isUnique = false;

    public boolean isPk() {
        return isPk;
    }

    public void setPk(boolean pk) {
        isPk = pk;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    /**
     * 新增索引字段
     * @param indexField 索引字段
     * @return 1 means fail, 0 means ok
     */
    public int addIndexField(IndexField indexField) {
        if (this.indexFields.add(indexField)) {
            return 0;
        }
        return 1;
    }

    public Set<IndexField> getIndexFields() {
        return this.indexFields;
    }

    public void setIndexFields(Set<IndexField> indexFields) {
        this.indexFields = indexFields;
    }

    /**
     * 获取索引值
     * @param data 索引名
     * @return 索引值
     */
    public Object[] getIndexValues(Object data) {
        int size = this.indexFields.size();
        Object[] values = new Object[size];

        int i = 0;
        for (IndexField field : this.indexFields) {
            values[i] = field.getDataField().getValue(data);
            if (values[i++] == null) {
                return null;
            }
        }
        return values;
    }
}
