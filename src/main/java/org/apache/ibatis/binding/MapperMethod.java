package org.apache.ibatis.binding;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/11
 * </p>
 */
public class MapperMethod {

    private final SqlCommand command;
    private final MethodSignature method;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        this.command = new SqlCommand(mapperInterface, method, config);
        this.method = new MethodSignature(config, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (command.getType()) {
            case INSERT: {
                Object param = method.convertArgsToSqlCommandParam(args);
                int rowCount = sqlSession.insert(command.getName(), param);
                result = rowCountResult(rowCount);
                break;
            }
            case UPDATE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                int rowCount = sqlSession.update(command.getName(), param);
                result = rowCountResult(rowCount);
                break;
            }
            case DELETE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                int rowCount = sqlSession.delete(command.getName(), param);
                result = rowCountResult(rowCount);
                break;
            }
            case SELECT: {
                if (method.returnsVoid()) {
                    Object param = method.convertArgsToSqlCommandParam(args);
                    sqlSession.selectOne(command.getName(), param);
                    return null;
                } else if (method.returnsMany()) {
                    Object param = method.convertArgsToSqlCommandParam(args);
                    result = sqlSession.selectList(command.getName(), param);
                } else {
                    Object param = method.convertArgsToSqlCommandParam(args);
                    Object r = sqlSession.selectOne(command.getName(), param);
                    if (method.returnsOptional()) {
                        result = Optional.ofNullable(r);
                    } else {
                        result = r;
                    }
                }
                break;
            }
            default:
                throw new BindingException("Unknown execution method for: " + command.getName());
        }
            //原始类型不能返回null
            if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
                throw new BindingException("Mapper method '" + command.getName()
                        + "' attempted to return null from a method with a primitive return type ("
                        + method.getReturnType() + ").");
            }
            return result;
    }
    private Object rowCountResult(int rowCount) {
        if (method.returnsVoid()) {
            return null;
        }
        Class<?> rt = method.getReturnType();
        if (rt == Integer.class || rt == int.class) {
            return rowCount;
        } else if (rt == Long.class || rt == long.class) {
            return (long) rowCount;
        } else if (rt == Boolean.class || rt == boolean.class) {
            return rowCount > 0;
        } else {
            // 其他返回类型，就直接给 int
            return rowCount;
        }
    }

        public static class SqlCommand {

        private final String name;

        private final SqlCommandType type;

        public SqlCommand(Class<?> mapperInterface, Method method,Configuration configuration) {
            final String methodName = method.getName();
            final Class<?> declaringClass = method.getDeclaringClass();
            MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);
            if (ms == null) {
                throw new BindingException(
                        "Invalid bound statement (not found): " + mapperInterface.getName() + "." + methodName);
            }else {
                name = ms.getId();
                type = ms.getSqlCommandType();
                if (type == SqlCommandType.UNKNOWN) {
                    throw new BindingException("Unknown execution method for: " + name);
                }
            }
        }
        private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringClass, Configuration configuration) {
            String statementId = mapperInterface.getName() + "." + methodName;
            if (configuration.hasStatement(statementId)) {
                return configuration.getMappedStatement(statementId);
            }
            if (mapperInterface.equals(declaringClass)){
                return null;
            }
            for (Class<?> superInterface : mapperInterface.getInterfaces()) {
                if (declaringClass.isAssignableFrom(superInterface)){
                    MappedStatement ms = resolveMappedStatement(superInterface, methodName, declaringClass, configuration);
                    if (ms != null) {
                        return ms;
                    }
                }
            }
            return null;
        }

            public SqlCommandType getType() {
                return type;
            }

            public String getName() {
                return name;
            }
        }

    public static class ParamMap<V> extends HashMap<String, V> {

        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + keySet());
            }
            return super.get(key);
        }

    }

    public static class MethodSignature {

        private final boolean returnsVoid;

        private final Class<?> returnType;

        private final boolean returnsOptional;

        private final ParamNameResolver paramNameResolver;

        private final boolean returnsMany;

        public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
            Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
            if (resolvedReturnType instanceof Class<?>) {
                this.returnType = (Class<?>) resolvedReturnType;
            } else if (resolvedReturnType instanceof ParameterizedType) {
                this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
            } else {
                this.returnType = method.getReturnType();
            }
            this.returnsOptional = Optional.class.equals(this.returnType);
            this.paramNameResolver = new ParamNameResolver(method);
            this.returnsVoid = void.class.equals(this.returnType);
            this.returnsMany = List.class.isAssignableFrom(this.returnType);
        }
        @SuppressWarnings("unchecked")
        public Object convertArgsToSqlCommandParam(Object[] args) {
            Object namedParams = paramNameResolver.getNamedParams(args);
            if (namedParams instanceof Map) {
                ParamMap<Object> paramMap = new ParamMap<>();
                paramMap.putAll((Map<String, Object>) namedParams);
                return paramMap;
            }
            return namedParams;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public boolean returnsVoid() {
            return returnsVoid;
        }

        public boolean returnsOptional() {
            return returnsOptional;
        }

        public boolean returnsMany() {
            return returnsMany;
        }
    }
}
