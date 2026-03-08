package org.apache.ibatis.type;

import org.apache.ibatis.io.Resources;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/17
 * </p>
 */
public class TypeAliasRegistry {
    private final Map<String, Class<?>> typeAliases = new HashMap<>();

    public TypeAliasRegistry() {
        // 基本类型及包装
        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("_byte", byte.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        // 常用引用类型
        registerAlias("string", String.class);
        registerAlias("date", java.util.Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);
        registerAlias("map", java.util.Map.class);
        registerAlias("hashmap", java.util.HashMap.class);
        registerAlias("list", java.util.List.class);
        registerAlias("arraylist", java.util.ArrayList.class);
        registerAlias("collection", java.util.Collection.class);
    }

    public void registerAlias(Class<?> type) {
        if (type == null) {
            return;
        }
        typeAliases.put(type.getSimpleName(), type);
    }

    public void registerAlias(String alias, Class<?> clazz) {
        if (alias == null) {
            throw new TypeException("The parameter alias cannot be null");
        }
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (typeAliases.containsKey(key)) {
            throw new TypeException("The alias '" + alias + "' already exists");
        }
        typeAliases.put(key, clazz);
    }
    @SuppressWarnings("unchecked")
    public <T> Class<T> resolveAlias(String alias) {
        if (alias == null) {
            throw new TypeException("The parameter alias cannot be null");
        }
        String key = alias.toLowerCase(Locale.ENGLISH);
        Class<?> clazz = typeAliases.get(key);
        if (clazz != null) {
            return (Class<T>) clazz;
        }
        // 不在别名表里，尝试当成全限定类名
        try {
            return (Class<T>) Resources.classForName(alias);
        } catch (ClassNotFoundException e) {
            throw new TypeException("Could not resolve type alias '" + alias + "'", e);
        }
    }
}
