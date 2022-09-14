package com.cracker.udfm.core;


/**
 * 类: 索引字段
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-16
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class IndexField {
    /**
     * 数据字段
     */
    private EntityField dataField = null;

    /**
     * 是否倒序
     */
    private boolean isDesc = false;


    public EntityField getDataField() {
        return dataField;
    }

    public void setDataField(EntityField dataField) {
        this.dataField = dataField;
    }

    public boolean isDesc() {
        return isDesc;
    }

    public void setDesc(boolean desc) {
        isDesc = desc;
    }

    /**
     * 当Map/Set的key为Object时，该Object需要重写equals和hashCode方法
     *
     * 1、当两个对象equals相等时，那么它们的hashCode一定相等
     * 2、当两个对象equals不等时，那么它们的hashCode不一定不等
     * 3、当两个对象的hashCode相等时，它们不一定equals相等
     * 4、当两个对象的hashCode不等时，它们一定equals不等
     *
     * @param object 等待判断的对象
     * @return 判断两个对象是否相等
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        IndexField other = (IndexField) object;
        if (this.getDataField() != null) {
            if (other.getDataField() != this.getDataField()) {
                return false;
            }
        }
        return this.isDesc() == other.isDesc();
    }


    /**
     * 当Map/Set的key为Object时，该Object需要重写equals和hashCode方法
     *
     * 1、当两个对象equals相等时，那么它们的hashCode一定相等
     * 2、当两个对象equals不等时，那么它们的hashCode不一定不等
     * 3、当两个对象的hashCode相等时，它们不一定equals相等
     * 4、当两个对象的hashCode不等时，它们一定equals不等
     *
     * @return int
     */
    @Override
    public int hashCode() {
        int h;
        if (this.getDataField() != null) {
            return ((h = this.getDataField().hashCode()) ^ (h >>> 16))
                    + Boolean.valueOf(this.isDesc()).hashCode();
        }
        return (h = Boolean.valueOf(this.isDesc()).hashCode()) ^ (h >>> 16);
    }


    public static void main(String[] args) {
//        IndexField indexField1 = new IndexField();
//        IndexField indexField2 = new IndexField();
//        System.out.println(indexField1.equals(indexField2));//true
//        System.out.println(indexField1.hashCode());//1237
//        System.out.println(indexField2.hashCode());//1237


//        indexField1.setDesc(true);
//        System.out.println(indexField1.equals(indexField2));//false
//        System.out.println(indexField1.hashCode());//1231
//        System.out.println(indexField2.hashCode());//1237


//        indexField1.setDataField(new EntityField());
//        System.out.println(indexField1.equals(indexField2));//false
//        System.out.println(indexField1.hashCode());//1259495633
//        System.out.println(indexField2.hashCode());//1237


//        indexField1.setDataField(new EntityField());
//        indexField2.setDataField(new EntityField());
//        System.out.println(indexField1.equals(indexField2));//false
//        System.out.println(indexField1.hashCode());//1259495633
//        System.out.println(indexField2.hashCode());//1300126413

    }

}
