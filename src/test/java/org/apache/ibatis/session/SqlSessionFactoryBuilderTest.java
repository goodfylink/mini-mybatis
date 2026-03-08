package org.apache.ibatis.session;

import org.apache.ibatis.io.Resources;
import org.junit.Test;
import java.io.Reader;

import static org.junit.Assert.*;

/**
 * Test cases for SqlSessionFactoryBuilder
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class SqlSessionFactoryBuilderTest {

    @Test
    public void testBuildFromXml() throws Exception {
        String resource = "mybatis-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        assertNotNull("Configuration XML file not found", reader);

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(reader);

        assertNotNull(factory);
        Configuration configuration = factory.getConfiguration();
        assertNotNull(configuration);

        // Basic configuration checks
        assertNotNull(configuration.getEnvironment());
        assertEquals("development", configuration.getEnvironment().getId());
        assertNotNull(configuration.getEnvironment().getDataSource());
    }
}
