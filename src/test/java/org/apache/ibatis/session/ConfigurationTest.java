package org.apache.ibatis.session;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for Configuration class
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class ConfigurationTest {

    @Test
    public void testConfigurationInitialization() {
        Configuration config = new Configuration();
        assertNotNull(config);
        assertNotNull(config.getTypeHandlerRegistry());
        assertNotNull(config.getTypeAliasRegistry());
    }

    @Test
    public void testAddMappedStatement() {
        Configuration config = new Configuration();
        MappedStatement ms = new MappedStatement.Builder(config, "testId", null, SqlCommandType.SELECT).build();
        config.addMappedStatement(ms);
        assertEquals(ms, config.getMappedStatement("testId"));
    }

    // Add more tests as needed
}