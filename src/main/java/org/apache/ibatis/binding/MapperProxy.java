package org.apache.ibatis.binding;

import org.apache.ibatis.MybatisException;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * </p>
 * Mapper 接口动态代理，然后执行方法
 * @author jcyin
 * @since 2026/2/11
 * </p>
 */
public class MapperProxy<T> implements InvocationHandler {

    //接口类型
    private final Class<T> mapperInterface;
    //会话对象
    private final SqlSession sqlSession;
    //方法缓存，避免重复创建
    private final Map<Method, MapperMethodInvoker> methodCache;

    public MapperProxy(SqlSession sqlSession,Class<T> mapperInterface,  Map<Method, MapperMethodInvoker> methodCache) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
        this.methodCache = methodCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            //如果是系统方法直接跳过
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            //从缓存中寻找避免二次创建，然后执行sql
            return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
        }catch (Throwable t){
            throw new MybatisException("Mapper proxy threw an exception", t);
        }
    }

    /**
     * 缓存MapperMethodInvoker避免重复创建
     * @param method
     * @return
     * @throws Throwable
     */
    private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
        MapperMethodInvoker invoker = methodCache.get(method);
        if (invoker != null) {
            return invoker;
        }
        try {
            //缓存不存在创建并且缓存
            return methodCache.computeIfAbsent(method, m -> {
                MapperMethod mapperMethod = new MapperMethod(mapperInterface, m, sqlSession.getConfiguration());
                return new PlainMethodInvoker(mapperMethod);
            });
        } catch (RuntimeException e) {
            throw new MybatisException("Failed to create MapperMethodInvoker", e);
        }
    }

    interface MapperMethodInvoker {
        Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable;
    }
    //执行MapperMethod，因为是mini版本，放弃DefaultMethodInvoker,即default修饰接口默认方法放弃支持
    private static class PlainMethodInvoker implements MapperMethodInvoker {

        private final MapperMethod mapperMethod;

        public PlainMethodInvoker(MapperMethod mapperMethod) {
            this.mapperMethod = mapperMethod;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
            return mapperMethod.execute(sqlSession, args);
        }
    }
}
