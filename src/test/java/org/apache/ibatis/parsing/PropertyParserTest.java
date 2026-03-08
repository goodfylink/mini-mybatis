package org.apache.ibatis.parsing;

import org.junit.Test;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for PropertyParser
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class PropertyParserTest {

    @Test
    public void testParse() {
        Properties props = new Properties();
        props.setProperty("username", "admin");
        props.setProperty("password", "123456");

        assertEquals("admin", PropertyParser.parse("${username}", props));
        assertEquals("Login with admin and 123456",
                PropertyParser.parse("Login with ${username} and ${password}", props));
    }

    @Test
    public void testParseWithMissingKey() {
        Properties props = new Properties();
        props.setProperty("username", "admin");

        // Key not found, should return original placeholder
        assertEquals("${password}", PropertyParser.parse("${password}", props));
    }

    @Test
    public void testParseWithNullProperties() {
        assertEquals("${username}", PropertyParser.parse("${username}", null));
    }
}
