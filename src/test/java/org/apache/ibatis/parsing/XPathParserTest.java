package org.apache.ibatis.parsing;

import org.junit.Test;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for XPathParser and XNode
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class XPathParserTest {

    @Test
    public void testEvalNodes() {
        String xml = "<root><item id='1'>one</item><item id='2'>two</item></root>";
        XPathParser parser = new XPathParser(new StringReader(xml));

        List<XNode> nodes = parser.evalNodes("/root/item");
        assertEquals(2, nodes.size());
        assertEquals("one", nodes.get(0).getStringBody());
        assertEquals("1", nodes.get(0).getStringAttribute("id"));
        assertEquals("two", nodes.get(1).getStringBody());
        assertEquals("2", nodes.get(1).getStringAttribute("id"));
    }

    @Test
    public void testEvalNode() {
        String xml = "<root><item id='1'>one</item></root>";
        XPathParser parser = new XPathParser(new StringReader(xml));

        XNode node = parser.evalNode("/root/item");
        assertNotNull(node);
        assertEquals("1", node.getStringAttribute("id"));
    }

    @Test
    public void testEvalString() {
        String xml = "<root><name>MyBatis</name></root>";
        XPathParser parser = new XPathParser(new StringReader(xml));

        assertEquals("MyBatis", parser.evalString("/root/name"));
    }

    @Test
    public void testEvalBoolean() {
        String xml = "<root><active>true</active></root>";
        XPathParser parser = new XPathParser(new StringReader(xml));

        assertTrue(parser.evalBoolean("/root/active"));
    }
}
