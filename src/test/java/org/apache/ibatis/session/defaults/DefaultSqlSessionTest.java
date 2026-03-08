package org.apache.ibatis.session.defaults;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for DefaultSqlSession class
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class DefaultSqlSessionTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Executor executor;

    private DefaultSqlSession sqlSession;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlSession = new DefaultSqlSession(configuration, executor);
    }

    @Test
    public void testSelectOne() throws Exception {
        MappedStatement ms = new MappedStatement.Builder(configuration, "test", null, SqlCommandType.SELECT).build();
        when(configuration.getMappedStatement("test")).thenReturn(ms);
        when(executor.query(ms, null)).thenReturn(Arrays.asList("result"));

        String result = sqlSession.selectOne("test");
        assertEquals("result", result);
    }

    @Test
    public void testSelectList() throws Exception {
        MappedStatement ms = new MappedStatement.Builder(configuration, "test", null, SqlCommandType.SELECT).build();
        when(configuration.getMappedStatement("test")).thenReturn(ms);
        List<Object> expected = Arrays.asList("result1", "result2");
        when(executor.query(ms, null)).thenReturn(expected);

        List<String> result = sqlSession.selectList("test");
        assertEquals(expected, result);
    }

    // Add more tests for insert, update, delete, commit, etc.
}