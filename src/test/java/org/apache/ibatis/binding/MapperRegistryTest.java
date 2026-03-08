package org.apache.ibatis.binding;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Test cases for MapperRegistry
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class MapperRegistryTest {

    private MapperRegistry registry;
    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration();
        registry = new MapperRegistry(config);
    }

    @Test
    public void testAddAndGetMapper() {
        registry.addMapper(BlogMapper.class);
        assertTrue(registry.hasMapper(BlogMapper.class));

        SqlSession sqlSession = Mockito.mock(SqlSession.class);
        Mockito.when(sqlSession.getConfiguration()).thenReturn(config);

        BlogMapper mapper = registry.getMapper(BlogMapper.class, sqlSession);
        assertNotNull(mapper);
    }

    @Test(expected = BindingException.class)
    public void testGetMapperMissing() {
        SqlSession sqlSession = Mockito.mock(SqlSession.class);
        registry.getMapper(BlogMapper.class, sqlSession);
    }

    @Test(expected = BindingException.class)
    public void testDuplicateAddMapper() {
        registry.addMapper(BlogMapper.class);
        registry.addMapper(BlogMapper.class);
    }

    @Test
    public void testAddNonInterfaceMapper() {
        registry.addMapper(BlogMapperImpl.class);
        assertFalse(registry.hasMapper(BlogMapperImpl.class));
    }

    // Dummy interfaces for testing
    public interface BlogMapper {
        String selectName(int id);
    }

    public static class BlogMapperImpl implements BlogMapper {
        @Override
        public String selectName(int id) {
            return null;
        }
    }
}
