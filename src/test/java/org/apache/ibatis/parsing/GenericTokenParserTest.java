package org.apache.ibatis.parsing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test cases for GenericTokenParser
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class GenericTokenParserTest {

    @Test
    public void testParse() {
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> content.toUpperCase());
        assertEquals("Hello WORLD", parser.parse("Hello #{world}"));
        assertEquals("Hello WORLD and MYBATIS", parser.parse("Hello #{world} and #{mybatis}"));
    }

    @Test
    public void testParseWithEscape() {
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> content.toUpperCase());
        // \#{world} -> #{world}
        assertEquals("Hello #{world}", parser.parse("Hello \\#{world}"));
    }

    @Test
    public void testParseWithUnclosedToken() {
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> content.toUpperCase());
        assertEquals("Hello #{world", parser.parse("Hello #{world"));
    }

    @Test
    public void testParseWithEmptyToken() {
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> "EMPTY");
        assertEquals("Hello EMPTY", parser.parse("Hello #{}"));
    }
}
