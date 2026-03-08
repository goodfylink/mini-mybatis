package org.apache.ibatis.reflection;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapper.ParamTestMapper;
import org.junit.Test;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public class ParamNameResolverTest {

    private ParamNameResolver newResolver(String methodName, Class<?>... paramTypes) throws NoSuchMethodException {
        Method method = ParamTestMapper.class.getMethod(methodName, paramTypes);
        return new ParamNameResolver(method);
    }

    @Test
    public void testOneParamNoAnnotation() throws Exception {
        ParamNameResolver oneParamNoAnnotation = newResolver("oneParamNoAnnotation", String.class);
        String[] names = oneParamNoAnnotation.getNames();
        System.out.println(Arrays.toString(names));
        assertArrayEquals(new String[]{"0"}, names);
    }


    @Test
    public void testGetNames_multiParamNoAnnotation() throws Exception {
        ParamNameResolver resolver = newResolver("multiParamNoAnnotation", String.class, Integer.class);
        String[] names = resolver.getNames();

        assertArrayEquals(new String[]{"0", "1"}, names);
    }

    @Test
    public void testGetNames_withParamAnnotation() throws Exception {
        ParamNameResolver resolver = newResolver("withParamAnnotation", Long.class, String.class);
        String[] names = resolver.getNames();
        System.out.println(Arrays.toString(names));
        assertArrayEquals(new String[]{"id", "name"}, names);
    }

    @Test
    public void testGetNames_partialParamAnnotation() throws Exception {
        ParamNameResolver resolver = newResolver("partialParamAnnotation", Long.class, String.class);
        String[] names = resolver.getNames();

        assertArrayEquals(new String[]{"id", "1"}, names);
    }

    @Test
    public void testGetNamedParams_singleParamNoAnnotation_returnValueItself() throws Exception {
        ParamNameResolver resolver = newResolver("oneParamNoAnnotation", String.class);
        Object[] args = new Object[]{"Tom"};
        Object result = resolver.getNamedParams(args);

        assertEquals("Tom", result);
    }

    @Test
    public void testGetNamedParams_singleCollectionParam_wrappedToMap() throws Exception {
        ParamNameResolver resolver = newResolver("oneCollectionParam", List.class);
        List<Long> ids = List.of(1L, 2L, 3L);
        Object result = resolver.getNamedParams(new Object[]{ids});

        assertTrue(result instanceof MapperMethod.ParamMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;

        assertSame(ids, map.get("collection"));
        assertSame(ids, map.get("list"));
    }

    @Test
    public void testGetNamedParams_multiParamNoAnnotation() throws Exception {
        ParamNameResolver resolver = newResolver("multiParamNoAnnotation", String.class, Integer.class);
        Object[] args = new Object[]{"Tom", 18};
        Object result = resolver.getNamedParams(args);

        assertTrue(result instanceof MapperMethod.ParamMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;

        // names: {0->"0", 1->"1"}
        assertEquals("Tom", map.get("0"));
        assertEquals(18, map.get("1"));

        // 同时有 param1 / param2
        assertEquals("Tom", map.get("param1"));
        assertEquals(18, map.get("param2"));
    }

    @Test
    public void testGetNamedParams_withParamAnnotation() throws Exception {
        ParamNameResolver resolver = newResolver("withParamAnnotation", Long.class, String.class);
        Object[] args = new Object[]{100L, "Tom"};
        Object result = resolver.getNamedParams(args);

        assertTrue(result instanceof MapperMethod.ParamMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;

        // 使用 @Param 指定的名字
        assertEquals(100L, map.get("id"));
        assertEquals("Tom", map.get("name"));

        // 也要有泛型名 param1 / param2
        assertEquals(100L, map.get("param1"));
        assertEquals("Tom", map.get("param2"));
    }

    @Test
    public void testGetNamedParams_partialParamAnnotation() throws Exception {
        ParamNameResolver resolver = newResolver("partialParamAnnotation", Long.class, String.class);
        Object[] args = new Object[]{100L, "Tom"};
        Object result = resolver.getNamedParams(args);

        assertTrue(result instanceof MapperMethod.ParamMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;

        // names: {0->"id", 1->"1"}
        assertEquals(100L, map.get("id"));
        assertEquals("Tom", map.get("1"));

        // 泛型名 param1 / param2
        assertEquals(100L, map.get("param1"));
        assertEquals("Tom", map.get("param2"));
    }

    @Test
    public void testGetNamedParams_noArgs() throws Exception {
        ParamNameResolver resolver = newResolver("oneParamNoAnnotation", String.class);
        Object result = resolver.getNamedParams(null);

        assertNull(result);
    }
}