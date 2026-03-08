package org.apache.ibatis.build;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/17
 * </p>
 */
public class BaseBuilder {
    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;
    protected final TypeHandlerRegistry typeHandlerRegistry;

    public BaseBuilder(Configuration configuration, TypeAliasRegistry typeAliasRegistry, TypeHandlerRegistry typeHandlerRegistry) {
        this.configuration = configuration;
        this.typeAliasRegistry = typeAliasRegistry;
        this.typeHandlerRegistry = typeHandlerRegistry;
    }

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }


    protected Pattern parseExpression(String regex, String defaultValue) {
        return Pattern.compile(regex == null ? defaultValue : regex);
    }

    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    protected Integer integerValueOf(String value, Integer defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    protected Set<String> stringSetValueOf(String value, String defaultValue) {
        value = value == null ? defaultValue : value;
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

    protected JdbcType resolveJdbcType(String alias) {
        try {
            return alias == null ? null : JdbcType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
        }
    }

    protected Object createInstance(String alias) {
        Class<?> clazz = resolveClass(alias);
        try {
            return clazz == null ? null : clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BuilderException("Error creating instance. Cause: " + e, e);
        }
    }

    protected <T> Class<? extends T> resolveClass(String alias) {
        try {
            return alias == null ? null : resolveAlias(alias);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlerAlias) {
        if (typeHandlerAlias == null) {
            return null;
        }
        Class<?> type = resolveClass(typeHandlerAlias);
        if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
            throw new BuilderException("Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
        }
        Class<? extends TypeHandler<?>> typeHandlerType = (Class<? extends TypeHandler<?>>) type;
        return resolveTypeHandler(javaType, typeHandlerType);
    }

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }
        TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
        return handler == null ? typeHandlerRegistry.getInstance(javaType,typeHandlerType) : handler;
    }
    protected <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}
