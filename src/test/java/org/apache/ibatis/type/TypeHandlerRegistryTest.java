package org.apache.ibatis.type;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for TypeHandlerRegistry
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class TypeHandlerRegistryTest {

    private TypeHandlerRegistry registry;

    @Before
    public void setUp() {
        registry = new TypeHandlerRegistry();
    }

    @Test
    public void testHasTypeHandler() {
        assertTrue(registry.hasTypeHandler(String.class));
        assertTrue(registry.hasTypeHandler(Integer.class));
        assertTrue(registry.hasTypeHandler(int.class));
        assertTrue(registry.hasTypeHandler(Long.class));
        assertTrue(registry.hasTypeHandler(Object.class)); // ObjectTypeHandler is registered by default
    }

    @Test
    public void testGetTypeHandler() {
        TypeHandler<?> handler = registry.getTypeHandler(String.class);
        assertNotNull(handler);
        assertTrue(handler instanceof StringTypeHandler);
    }

    @Test
    public void testGetTypeHandlerWithJdbcType() {
        // Test retrieval with specific JdbcType
        TypeHandler<?> handler = registry.getTypeHandler(String.class, JdbcType.VARCHAR);
        assertNotNull(handler);

        // Test retrieval with null JdbcType (should still work after our fix)
        TypeHandler<?> handlerNull = registry.getTypeHandler(String.class, null);
        assertNotNull(handlerNull);
    }

    @Test
    public void testRegisterAndGet() {
        TypeHandler<Long> handler = new BaseTypeHandler<Long>() {
            @Override
            public void setNonNullParameter(java.sql.PreparedStatement ps, int i, Long parameter, JdbcType jdbcType)
                    throws java.sql.SQLException {
                ps.setLong(i, parameter);
            }

            @Override
            public Long getNullableResult(java.sql.ResultSet rs, String columnName) throws java.sql.SQLException {
                return rs.getLong(columnName);
            }

            @Override
            public Long getNullableResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
                return rs.getLong(columnIndex);
            }

            @Override
            public Long getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
                return cs.getLong(columnIndex);
            }
        };

        registry.register(Long.class, handler);
        assertTrue(registry.hasTypeHandler(Long.class));
        assertEquals(handler, registry.getTypeHandler(Long.class));
    }
}
