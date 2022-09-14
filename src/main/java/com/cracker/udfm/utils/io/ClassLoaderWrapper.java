package com.cracker.udfm.utils.io;

import com.cracker.udfm.utils.Assert;

import java.io.InputStream;
import java.net.URL;

/**
 * 类加载器包裹类
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-06
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ClassLoaderWrapper {

    /**
     * 应用程序类加载器
     */
    ClassLoader systemClassLoader;
    /**
     * 启动类加载器
     */
    ClassLoader contextClassLoader;
    
    ClassLoaderWrapper() {
        try {
            // 应用程序类加载器
            this.systemClassLoader = ClassLoader.getSystemClassLoader();
            // 启动类加载器
            this.contextClassLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access system or context ClassLoader
        }
    }
    
    public URL getResourceAsUrl(String resource) {
        return this.getResourceAsUrl(resource, this.getClassLoaders(null));
    }
    
    public URL getResourceAsUrl(String resource, ClassLoader classLoader) {
        return this.getResourceAsUrl(resource, this.getClassLoaders(classLoader));
    }

    public InputStream getResourceAsStream(String resource) {
        return this.getResourceAsStream(resource, this.getClassLoaders(null));
    }
    
    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return this.getResourceAsStream(resource, this.getClassLoaders(classLoader));
    }

    public InputStream getResourceAsStream(String resource, ClassLoader[] classLoaders) {
        if (Assert.isEmpty(resource) || !Assert.isNotNull(classLoaders)) {
            throw new IllegalArgumentException();
        }
        InputStream returnValue;
        for (ClassLoader classLoader : classLoaders) {

            if (classLoader == null) {
                continue;
            }
            
            if ((returnValue = classLoader.getResourceAsStream(resource)) != null) {
                return returnValue;
            }

            if ((returnValue = classLoader.getResourceAsStream("/" + resource)) != null) {
                return returnValue;
            }

        }
        return null;
    }

    public URL getResourceAsUrl(String resource, ClassLoader[] classLoaders) {
        if (Assert.isEmpty(resource) || !Assert.isNotNull(classLoaders)) {
            throw new IllegalArgumentException();
        }
        URL url;
        for (ClassLoader classLoader : classLoaders) {

            if (classLoader == null) {
                continue;
            }
            
            if ((url = classLoader.getResource(resource)) != null) {
                return url;
            }

            if ((url = classLoader.getResource("/" + resource)) != null) {
                return url;
            }
        }

        return null;
    }
    
    
    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        // 返回顺序，自定义的classLoader-->启动类加载器-->扩展类加载器-->应用程序类加载器
        return new ClassLoader[] {classLoader, this.contextClassLoader, 
                ClassLoaderWrapper.class.getClassLoader(), this.systemClassLoader};
    }
    
    public Class<?> classForName(String name) throws ClassNotFoundException {
        return this.classForName(name, this.getClassLoaders(null));
    }
    
    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return this.classForName(name, this.getClassLoaders(classLoader));
    }
    
    Class<?> classForName(String name, ClassLoader[] classLoaders) throws ClassNotFoundException {
        Assert.checkArgument(classLoaders != null && classLoaders.length > 0,
                "ClassLoader array is empty ... ");
        for (ClassLoader loader : classLoaders) {
            try {
                // true: 表示既将.class文件加载到jvm中，又执行static静态代码块
                return Class.forName(name, true, loader);
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }
        
        throw new ClassNotFoundException("Cannot find class: " + name);
    }
}
