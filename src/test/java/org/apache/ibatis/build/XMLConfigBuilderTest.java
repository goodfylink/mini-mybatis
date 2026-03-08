//package org.apache.ibatis.build;
//
//import org.apache.ibatis.session.Configuration;
//import org.junit.Test;
//import org.xml.sax.InputSource;
//
//import java.io.StringReader;
//
//import static org.junit.Assert.*;
//
///**
// * Test cases for XMLConfigBuilder class
// *
// * @author jcyin
// * @since 2026/2/21
// */
//public class XMLConfigBuilderTest {
//
//    @Test
//    public void testParseConfiguration() {
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
//                     "<configuration>\n" +
//                     "  <environments default=\"development\">\n" +
//                     "    <environment id=\"development\">\n" +
//                     "      <transactionManager type=\"JDBC\"/>\n" +
//                     "      <dataSource type=\"UNPOOLED\">\n" +
//                     "        <property name=\"driver\" value=\"org.h2.Driver\"/>\n" +
//                     "        <property name=\"url\" value=\"jdbc:h2:mem:test\"/>\n" +
//                     "      </dataSource>\n" +
//                     "    </environment>\n" +
//                     "  </environments>\n" +
//                     "</configuration>";
//        XMLConfigBuilder builder = new XMLConfigBuilder(new InputSource(new StringReader(xml)));
//        Configuration config = builder.parse();
//        assertNotNull(config);
//        assertNotNull(config.getEnvironment());
//    }
//}