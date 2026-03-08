package org.apache.ibatis.build;

import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Test cases for MapperBuilderAssistant
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class MapperBuilderAssistantTest {

    private MapperBuilderAssistant assistant;
    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = new Configuration();
        assistant = new MapperBuilderAssistant(configuration, "test-resource.xml");
    }

    @Test
    public void testSetCurrentNamespace() {
        assistant.setCurrentNamespace("com.example.UserMapper");
        assertEquals("com.example.UserMapper", assistant.getCurrentNamespace());
    }

    @Test(expected = BuilderException.class)
    public void testSetWrongNamespace() {
        assistant.setCurrentNamespace("com.example.UserMapper");
        assistant.setCurrentNamespace("com.example.OtherMapper"); // Should throw exception
    }

    @Test
    public void testApplyCurrentNamespace() {
        assistant.setCurrentNamespace("com.example.UserMapper");

        // Short ID
        assertEquals("com.example.UserMapper.selectUser", assistant.applyCurrentNamespace("selectUser", false));

        // Reference with existing namespace
        assertEquals("org.other.Mapper.selectOther",
                assistant.applyCurrentNamespace("org.other.Mapper.selectOther", true));
    }

    @Test
    public void testAddMappedStatement() {
        assistant.setCurrentNamespace("com.example.UserMapper");
        SqlSource sqlSource = Mockito.mock(SqlSource.class);

        assistant.addMappedStatement(
                "getUser",
                sqlSource,
                SqlCommandType.SELECT,
                null,
                String.class);

        assertTrue(configuration.hasStatement("com.example.UserMapper.getUser"));
    }
}
