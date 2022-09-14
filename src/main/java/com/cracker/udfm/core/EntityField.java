package com.cracker.udfm.core;

import com.cracker.udfm.common.DbType;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 数据存储字段
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-05
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class EntityField {
    /**
     * 字段元数据
     */
    private Field fieldMeta;
    /**
     * 存储名称
     */
    private String columnName;
    /**
     * 是否主键
     */
    private boolean isPk;
    /**
     * 是否自增Id
     */
    private boolean isAutoGenerated;
    /**
     * 是否引用类型
     */
    private boolean isRefType;
    /**
     * 字段类型
     */
    private DbType dbType;
    /**
     * 是否为ID
     */
    private boolean isId = false;
    /**
     * 是否为自动增长字段
     */
    private boolean isAutoIncrease = false;
    /**
     * 是否允许存储空值
     */
    private boolean storeNull = false;
    /**
     * 是否唯一
     */
    private boolean isUnique = false;
    /**
     * 子类实体
     */
    private Entity subClass;
    /**
     * 父类实体
     */
    private Entity motherClass;
    /**
     * 数据字段类型，其取值范围为：枚举值，参见DataFieldType定义
     */
    private DataFieldType decType = DataFieldType.ATOM;

    public Field getFieldMeta() {
        return fieldMeta;
    }

    public void setFieldMeta(Field fieldMeta) {
        this.fieldMeta = fieldMeta;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isPk() {
        return isPk;
    }

    public void setPk(boolean pk) {
        isPk = pk;
    }

    public boolean isAutoGenerated() {
        return isAutoGenerated;
    }

    public void setAutoGenerated(boolean autoGenerated) {
        isAutoGenerated = autoGenerated;
    }

    public boolean isRefType() {
        return isRefType;
    }

    public void setRefType(boolean refType) {
        isRefType = refType;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean id) {
        isId = id;
    }

    public boolean isAutoIncrease() {
        return isAutoIncrease;
    }

    public void setAutoIncrease(boolean autoIncrease) {
        isAutoIncrease = autoIncrease;
    }

    public boolean isStoreNull() {
        return storeNull;
    }

    public void setStoreNull(boolean storeNull) {
        this.storeNull = storeNull;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public Entity getSubClass() {
        return subClass;
    }

    public void setSubClass(Entity subClass) {
        this.subClass = subClass;
    }

    public Entity getMotherClass() {
        return motherClass;
    }

    public void setMotherClass(Entity motherClass) {
        this.motherClass = motherClass;
    }

    public DataFieldType getDecType() {
        return decType;
    }

    public void setDecType(DataFieldType decType) {
        this.decType = decType;
    }

    public int setValue(Object obj, Object val) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldMeta.getName(), obj.getClass());
            Method methodSet = pd.getWriteMethod();
            methodSet.invoke(obj, val);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public Object getValue(Object obj) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldMeta.getName(), obj.getClass());
            Method methodGet = pd.getReadMethod();
            return methodGet.invoke(obj);
        } catch(IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return 1;
        }
    }
}