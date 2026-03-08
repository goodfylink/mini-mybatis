package org.apache.ibatis.reflection;

import org.apache.ibatis.mapper.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for MetaObject class, covering JavaBeans and Maps with nested
 * properties.
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class MetaObjectTest {

    @Test
    public void testJavaBeanSimpleProperty() {
        User user = new User();
        MetaObject metaObject = new MetaObject(user);
        metaObject.setValue("name", "John");
        assertEquals("John", user.getName());
        assertEquals("John", metaObject.getValue("name"));
    }

    @Test
    public void testMapSimpleProperty() {
        Map<String, Object> map = new HashMap<>();
        MetaObject metaObject = new MetaObject(map);
        metaObject.setValue("key", "value");
        assertEquals("value", map.get("key"));
        assertEquals("value", metaObject.getValue("key"));
    }

    @Test
    public void testNestedJavaBeanProperty() {
        Blog blog = new Blog();
        blog.setAuthor(new User(1, "Author", 30));
        MetaObject metaObject = new MetaObject(blog);

        assertEquals("Author", metaObject.getValue("author.name"));

        metaObject.setValue("author.name", "New Author");
        assertEquals("New Author", blog.getAuthor().getName());
    }

    @Test
    public void testNestedMapProperty() {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("info", "secret");
        outerMap.put("data", innerMap);

        MetaObject metaObject = new MetaObject(outerMap);
        assertEquals("secret", metaObject.getValue("data.info"));

        metaObject.setValue("data.info", "public");
        assertEquals("public", innerMap.get("info"));
    }

    @Test
    public void testMixedNestedProperty() {
        // Map containing a JavaBean
        Map<String, Object> map = new HashMap<>();
        map.put("user", new User(2, "Mixed", 25));

        MetaObject metaMap = new MetaObject(map);
        assertEquals("Mixed", metaMap.getValue("user.name"));

        metaMap.setValue("user.name", "Updated Mixed");
        assertEquals("Updated Mixed", ((User) map.get("user")).getName());

        // JavaBean containing a Map
        Blog blog = new Blog();
        Map<String, String> extraInfo = new HashMap<>();
        extraInfo.put("tags", "java,mybatis");
        blog.setExtraInfo(extraInfo);

        MetaObject metaBlog = new MetaObject(blog);
        assertEquals("java,mybatis", metaBlog.getValue("extraInfo.tags"));

        metaBlog.setValue("extraInfo.tags", "mybatis-mini");
        assertEquals("mybatis-mini", extraInfo.get("tags"));
    }

    // Helper classes for nested testing
    public static class Blog {
        private User author;
        private Map<String, String> extraInfo;

        public User getAuthor() {
            return author;
        }

        public void setAuthor(User author) {
            this.author = author;
        }

        public Map<String, String> getExtraInfo() {
            return extraInfo;
        }

        public void setExtraInfo(Map<String, String> extraInfo) {
            this.extraInfo = extraInfo;
        }
    }
}
