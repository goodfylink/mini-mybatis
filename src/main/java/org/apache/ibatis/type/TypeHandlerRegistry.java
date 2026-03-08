package org.apache.ibatis.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/16
 *        </p>
 */
public final class TypeHandlerRegistry {

    private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();

    private final Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMap = new EnumMap<>(JdbcType.class);

    private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();

    public TypeHandlerRegistry() {
        register(Boolean.class, new BooleanTypeHandler());
        register(boolean.class, new BooleanTypeHandler());
        register(JdbcType.BOOLEAN, new BooleanTypeHandler());
        register(JdbcType.BIT, new BooleanTypeHandler());

        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(JdbcType.INTEGER, new IntegerTypeHandler());

        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());

        register(String.class, new StringTypeHandler());
        register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
        register(String.class, JdbcType.LONGVARCHAR, new StringTypeHandler());
        register(JdbcType.CHAR, new StringTypeHandler());
        register(JdbcType.VARCHAR, new StringTypeHandler());
        register(String.class, JdbcType.CHAR, new StringTypeHandler());
        register(JdbcType.BIGINT, new LongTypeHandler());
        register(Object.class, new ObjectTypeHandler());

        register(Double.class, new DoubleTypeHandler());
        register(double.class, new DoubleTypeHandler());
        register(JdbcType.DOUBLE, new DoubleTypeHandler());

        register(Date.class, new DateTypeHandler());

        register(Character.class, new CharacterTypeHandler());
        register(char.class, new CharacterTypeHandler());

    }

    public <T> void register(Class<T> javaType, TypeHandler<? extends T> handler) {
        register((Type) javaType, null, handler);
    }

    // javaType + jdbcType -> handler
    public <T> void register(Class<T> javaType, JdbcType jdbcType, TypeHandler<? extends T> handler) {
        register((Type) javaType, jdbcType, handler);
    }

    public void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
        if (javaType == null || handler == null) {
            return;
        }
        Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaType);
        if (map == null) {
            map = new HashMap<>();
            typeHandlerMap.put(javaType, map);
        }
        map.put(jdbcType, handler);
        allTypeHandlersMap.put(handler.getClass(), handler);
    }

    public void register(JdbcType jdbcType, TypeHandler<?> handler) {
        if (jdbcType == null || handler == null) {
            return;
        }
        jdbcTypeHandlerMap.put(jdbcType, handler);
    }

    public <T> TypeHandler<T> getTypeHandler(Class<T> javaType) {
        return getTypeHandler(javaType, null);
    }

    public <T> TypeHandler<T> getTypeHandler(Class<T> javaType, JdbcType jdbcType) {
        if (javaType != null) {
            Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaType);
            if (map != null) {
                TypeHandler<?> handler = map.get(jdbcType);
                if (handler != null) {
                    return (TypeHandler<T>) handler;
                }
                TypeHandler<?> typeHandler = map.get(null);
                if (typeHandler != null) {
                    return (TypeHandler<T>) typeHandler;
                }
                if (map.size() == 1) {
                    return (TypeHandler<T>) map.values().iterator().next();
                }
            }
        }
        if (jdbcType != null) {
            TypeHandler<?> typeHandler = jdbcTypeHandlerMap.get(jdbcType);
            if (typeHandler != null) {
                return (TypeHandler<T>) typeHandler;
            }
        }
        return null;
    }

    public boolean hasTypeHandler(Class<?> javaType) {
        return hasTypeHandler(javaType, null);
    }

    public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
        return getTypeHandler(javaType, jdbcType) != null;
    }

    public TypeHandler<?> getMappingTypeHandler(Class<? extends TypeHandler<?>> handlerType) {
        return allTypeHandlersMap.get(handlerType);
    }

    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException e) {
                //
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
        }
    }
}
