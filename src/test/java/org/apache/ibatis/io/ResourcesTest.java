package org.apache.ibatis.io;

import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test cases for Resources class
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class ResourcesTest {

    @Test
    public void testGetResourceAsStream() throws IOException {
        // Test with existing resource
        InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
        assertNotNull(in);
        in.close();
    }

    @Test(expected = IOException.class)
    public void testGetResourceAsStreamNotFound() throws IOException {
        Resources.getResourceAsStream("non-existent.xml");
    }

    @Test
    public void testGetResourceAsProperties() throws IOException {
        Properties props = Resources.getResourceAsProperties("mybatis-config.xml");
        assertNotNull(props);
    }

    @Test
    public void testClassForName() throws ClassNotFoundException {
        Class<?> clazz = Resources.classForName("org.apache.ibatis.io.Resources");
        assertEquals(Resources.class, clazz);
    }
}
