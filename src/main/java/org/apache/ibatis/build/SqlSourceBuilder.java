package org.apache.ibatis.build;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 *        </p>
 */
public class SqlSourceBuilder extends BaseBuilder {

    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);
        return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private List<ParameterMapping> parameterMappings = new ArrayList<>();
        private Class<?> parameterType;

        public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType) {
            super(configuration);
            this.parameterType = parameterType;
        }

        public List<ParameterMapping> getParameterMappings() {
            return parameterMappings;
        }

        @Override
        public String handleToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }

        private ParameterMapping buildParameterMapping(String content) {
            Class<?> propertyType = Object.class;
            if (parameterType != null) {
                if (java.util.Map.class.isAssignableFrom(parameterType)) {
                    propertyType = Object.class;
                } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterType)) {
                    propertyType = parameterType;
                } else {
                    // If it's a bean, try to find the property type
                    try {
                        java.lang.reflect.Field field = parameterType.getDeclaredField(content);
                        propertyType = field.getType();
                    } catch (NoSuchFieldException e) {
                        // Try getter
                        String methodName = "get" + Character.toUpperCase(content.charAt(0))
                                + content.substring(content.length() > 1 ? 1 : 0);
                        if (content.length() == 1) {
                            methodName = "get" + Character.toUpperCase(content.charAt(0));
                        }
                        try {
                            java.lang.reflect.Method method = parameterType.getMethod(methodName);
                            propertyType = method.getReturnType();
                        } catch (NoSuchMethodException e2) {
                            propertyType = Object.class;
                        }
                    }
                }
            }
            return new ParameterMapping.Builder(configuration, content, propertyType).build();
        }
    }
}
