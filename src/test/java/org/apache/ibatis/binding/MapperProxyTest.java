package org.apache.ibatis.binding;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Test cases for MapperProxy class
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class MapperProxyTest {

    @Mock
    private SqlSession sqlSession;

    @Mock
    private org.apache.ibatis.session.Configuration configuration;

    private MapperProxy<TestMapper> mapperProxy;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sqlSession.getConfiguration()).thenReturn(configuration);
        Map<java.lang.reflect.Method, MapperProxy.MapperMethodInvoker> methodCache = new HashMap<>();
        mapperProxy = new MapperProxy<>(sqlSession, TestMapper.class, methodCache);
    }

    @Test
    public void testInvoke() throws Exception {
        TestMapper mapper = (TestMapper) Proxy.newProxyInstance(
                TestMapper.class.getClassLoader(),
                new Class<?>[] { TestMapper.class },
                mapperProxy);

        String statementId = TestMapper.class.getName() + ".testSelect";
        org.apache.ibatis.mapping.MappedStatement ms = new org.apache.ibatis.mapping.MappedStatement.Builder(
                configuration, statementId, null, org.apache.ibatis.mapping.SqlCommandType.SELECT).build();

        // Mock the statement ID lookup in configuration
        when(configuration.hasStatement(statementId)).thenReturn(true);
        when(configuration.getMappedStatement(statementId)).thenReturn(ms);

        // Mock the method invocation
        when(sqlSession.selectOne(eq(statementId), any())).thenReturn("result");

        String result = mapper.testSelect();
        assertEquals("result", result);
    }

    // Define a test mapper interface
    interface TestMapper {
        String testSelect();
    }
}