package com.cracker.udfm.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;
import java.io.File;

/**
 * 资源读取类
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-06
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class Resources {
    
    private static final ClassLoaderWrapper CLASS_LOADER_WRAPPER = new ClassLoaderWrapper();
    private static Charset charset;
    
    Resources() {
    }
    
    public static URL getResourceUrl(String resource) throws IOException {
        return getResourceUrl(null, resource);
    }
    
    public static URL getResourceUrl(ClassLoader classLoader, String resource) throws IOException {
        URL url = CLASS_LOADER_WRAPPER.getResourceAsUrl(resource, classLoader);
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }
    
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    public static InputStream getResourceAsStream(ClassLoader classloader, String resource)
            throws IOException {
        InputStream in = CLASS_LOADER_WRAPPER.getResourceAsStream(resource, classloader);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    public synchronized static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource);
        props.load(in);
        in.close();
        return props;
    }

    public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(loader, resource);
        props.load(in);
        in.close();
        return props;
    }

    public static Reader getResourceAsReader(String resource) throws IOException {
        InputStreamReader reader;
        if (charset == null) {
            return new InputStreamReader(getResourceAsStream(resource));
        }
        return new InputStreamReader(getResourceAsStream(resource), charset);
    }

    public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
        InputStreamReader reader;
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(loader, resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(loader, resource), charset);
        }

        return reader;
    }

    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceUrl(resource).getFile());
    }

    public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
        return new File(getResourceUrl(loader, resource).getFile());
    }

    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }
    
    public static Reader getUrlAAsReader(String urlString) throws IOException {
        InputStreamReader reader;
        if (charset == null) {
            reader = new InputStreamReader(getUrlAsStream(urlString));
        } else {
            reader = new InputStreamReader(getUrlAsStream(urlString), charset);
        }
        
        return reader;
    }
    
    public static Properties getUrlAsProperties(String urlString) throws IOException {
        Properties props = new Properties();
        InputStream in = getUrlAsStream(urlString);
        props.load(in);
        in.close();
        return props;
    }
    
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return CLASS_LOADER_WRAPPER.classForName(className);
    }
    
    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        Resources.charset = charset;
    }
}
