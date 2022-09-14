package com.cracker.udfm.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * String和Object value判断类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2021-08-02
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class Assert {

    private static final String QUOTES = "";

    public static boolean isEmpty(String var) {
        return var == null || var.length() == 0 || QUOTES.equals(var.trim());
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNotNull(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Optional) {
            // 返回true，非null
            return ((Optional) object).isPresent();
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) != 0;
        } else if (object instanceof CharSequence) {
            return ((CharSequence) object).length() > 0;
        } else if (object instanceof Collection) {
            return !((Collection) object).isEmpty();
        } else {
            // 假如不是Map及其子类, 那么经过前面的判断, 可以认为该Object非空
            return !(object instanceof Map) || !((Map) object).isEmpty();
        }
    }

    public static void notEmpty(String var, String message) {
        if (var == null
                || QUOTES.equals(var.trim())) {
            throw new IllegalArgumentException(message);
        }
        requireNonEmpty(var, message);
    }

    public static <T> T requireNonEmpty(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        if (isNotNull(obj)) {
            return obj;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 断言，判断condition条件是否为false
     * @param condition 判断condition
     * @param notice notice
     */
    public static void checkArgument(boolean condition, String notice) {
        if (!condition) {
            throw new IllegalArgumentException(notice);
        }
    }
}