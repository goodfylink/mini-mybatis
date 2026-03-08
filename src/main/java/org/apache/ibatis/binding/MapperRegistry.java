package org.apache.ibatis.binding;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/17
 *        </p>
 */
public class MapperRegistry {
    private final Configuration config;

    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new ConcurrentHashMap<>();

    public MapperRegistry(Configuration config) {
        this.config = config;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e);
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public <T> void addMapper(Class<T> type) {
        if (!type.isInterface()) {
            return;
        }
        if (hasMapper(type)) {
            throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
        }
        knownMappers.put(type, new MapperProxyFactory<>(type));
    }
}
