package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Test cases for MappedStatement and its Builder
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class MappedStatementTest {

    @Test
    public void testBuilder() {
        Configuration config = new Configuration();
        SqlSource sqlSource = Mockito.mock(SqlSource.class);
        String id = "com.example.UserMapper.getUser";

        MappedStatement.Builder builder = new MappedStatement.Builder(config, id, sqlSource, SqlCommandType.SELECT);
        builder.resultType(String.class);

        MappedStatement ms = builder.build();

        assertEquals(id, ms.getId());
        assertEquals(config, ms.getConfiguration());
        assertEquals(sqlSource, ms.getSqlSource());
        assertEquals(SqlCommandType.SELECT, ms.getSqlCommandType());

        assertNotNull(ms.getResultMaps());
        assertEquals(1, ms.getResultMaps().size());
        assertEquals(String.class, ms.getResultMaps().get(0).getType());
    }

    @Test
    public void testBuilderResultTypeNull() {
        Configuration config = new Configuration();
        SqlSource sqlSource = Mockito.mock(SqlSource.class);
        MappedStatement.Builder builder = new MappedStatement.Builder(config, "test", sqlSource, SqlCommandType.INSERT);

        builder.resultType(null); // Should handle null gracefully
        MappedStatement ms = builder.build();
        assertTrue(ms.getResultMaps().isEmpty());
    }
}
