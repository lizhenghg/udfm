
package com.cracker.udfm.exception;

import java.util.concurrent.atomic.AtomicLong;


/**
 * base exception
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-05
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class BaseUidmException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());
    String id;

    public BaseUidmException() {
        this.setId();
    }

    public BaseUidmException(String message) {
        super(message);
        this.setId();
    }

    public BaseUidmException(Throwable cause) {
        super(cause);
    }

    public BaseUidmException(String message, Throwable cause) {
        super(message, cause);
        this.setId();
    }

    void setId() {
        long nid = atomicLong.incrementAndGet();
        this.id = Long.toString(nid, 26);
    }

    public abstract String getErrorTitle();

    public abstract String getErrorDescription();

    public boolean isSourceAvailable() {
        return this instanceof SourceAttachment;
    }

    public Integer getLineNumber() {
        return -1;
    }

    public String getSourceFile() {
        return "";
    }

    public String getId() {
        return this.id;
    }

    public String getMoreHTML() {
        return null;
    }
}
