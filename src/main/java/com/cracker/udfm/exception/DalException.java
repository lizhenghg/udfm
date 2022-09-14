package com.cracker.udfm.exception;

import com.cracker.udfm.utils.Logger;

/**
 * 目标元数据信息
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-05
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class DalException extends RuntimeException {

    private String message;
    private Throwable throwable;

    public boolean isLogged = false;

    public DalException() {}

    public DalException(String message) {
        super(message);
        this.setMessage(message);
    }

    public DalException(String message, Throwable throwable) {
        super(message, throwable);
        this.setMessage(message);
        this.setThrowable(throwable);
    }

    /** 错误信息格式化封装 */
    public void logError(Exception e, String message) {
        // 错误信息格式
        try {
            StringBuilder error = new StringBuilder();
            error.append("\n=========================================\n");
            error.append("[Dal ERROR]\n");
            error.append(message).append("\n");
            if (e != null) {
                error.append("[Catch]\n");
                error.append(e.toString()).append("\n");
                error.append("[Stack]\n");
                for (StackTraceElement trace : e.getStackTrace()) {
                    error.append("at ");
                    error.append(trace.getClassName()).append(".").append(trace.getMethodName());
                    error.append("(").append(trace.getFileName()).append(":").append(trace.getLineNumber()).append(")\n");
                }
            }
            error.append("=========================================\n");
            if (!this.isLogged) {
                Logger.error(error.toString());
                this.isLogged = true;
            }
        }
        catch (Exception er) {
            Logger.error("dal-log writing error: " + er.toString());
        }
    }


    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }
}
