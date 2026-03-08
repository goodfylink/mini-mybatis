package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/13
 *        </p>
 */
public class ParameterMapping {

    private Configuration configuration;

    private String property;
    private Class<?> javaType = Object.class;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;

    private ParameterMapping() {

    }

    public static class Builder {

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, TypeHandler<?> typeHandler) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.typeHandler = typeHandler;
        }

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            parameterMapping.typeHandler = typeHandler;
            return this;
        }

        public ParameterMapping build() {
            resolveTypeHandler();
            validate();
            return parameterMapping;
        }

        private void validate() {
            if (parameterMapping.typeHandler == null) {
                throw new IllegalStateException("Type handler was null on parameter mapping for property '"
                        + parameterMapping.property
                        + "'. It was either not specified and/or could not be found for the javaType ("
                        + parameterMapping.javaType.getName() + ") : jdbcType (" + parameterMapping.jdbcType
                        + ") combination.");
            }
        }

        private void resolveTypeHandler() {
            if (parameterMapping.typeHandler == null) {
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType,
                        parameterMapping.jdbcType);
            }
        }
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public String getProperty() {
        return property;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    @Override
    public String toString() {
        return "ParameterMapping{" +
                "configuration=" + configuration +
                ", property='" + property + '\'' +
                ", javaType=" + javaType +
                ", jdbcType=" + jdbcType +
                ", typeHandler=" + typeHandler +
                '}';
    }
}
