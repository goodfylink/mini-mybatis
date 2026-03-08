package org.apache.ibatis.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/17
 * </p>
 */
public class Resources {
    private static final ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    private Resources() {

    }

    public static ClassLoader getDefaultClassLoader() {
        return classLoaderWrapper.defaultClassLoader;
    }

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        classLoaderWrapper.defaultClassLoader = defaultClassLoader;
    }

    public static URL getResourceURL(String resource) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource);
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    public static Reader getResourceAsReader(String resource) throws IOException {
        Reader reader;
        reader = new InputStreamReader(getResourceAsStream(resource));

        return reader;
    }

    public static URL getResourceURL(ClassLoader loader, String resource) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource, loader);
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        try (InputStream in = getResourceAsStream(resource)) {
            props.load(in);
        }
        return props;
    }

    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }
}
