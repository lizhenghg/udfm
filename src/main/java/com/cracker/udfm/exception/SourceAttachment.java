package com.cracker.udfm.exception;

import java.util.List;

/**
 *
 * 源附件接口
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-05
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface SourceAttachment {

    /**
     * 获取源文件名
     * @return String
     */
    String getSourceFile();

    /**
     * 获取全部的源文件名
     * @return List<String>
     */
    List<String> getSource();

    /**
     * line number
     * @return Integer
     */
    Integer getLineNumber();

}
