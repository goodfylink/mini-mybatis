package org.apache.ibatis.mapping;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public class ResultMapping {

    private final String property;          // Java 属性名
    private final String column;            // DB 列名
    private final Class<?> javaType;        // Java 类型
    private final JdbcType jdbcType;        // JDBC 类型，可为 null
    private final TypeHandler<?> typeHandler;

    public static class Builder {
        private String property;
        private String column;
        private Class<?> javaType;
        private JdbcType jdbcType;
        private TypeHandler<?> typeHandler;

        public Builder property(String property) {
            this.property = property;
            return this;
        }

        public Builder column(String column) {
            this.column = column;
            return this;
        }

        public Builder javaType(Class<?> javaType) {
            this.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            this.jdbcType = jdbcType;
            return this;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            this.typeHandler = typeHandler;
            return this;
        }

        public ResultMapping build() {
            if (property == null || column == null) {
                throw new IllegalArgumentException("ResultMapping requires both property and column.");
            }
            return new ResultMapping(property, column, javaType, jdbcType, typeHandler);
        }
    }

    private ResultMapping(String property,
                          String column,
                          Class<?> javaType,
                          JdbcType jdbcType,
                          TypeHandler<?> typeHandler) {
        this.property = property;
        this.column = column;
        this.javaType = javaType;
        this.jdbcType = jdbcType;
        this.typeHandler = typeHandler;
    }

    public String getProperty() {
        return property;
    }

    public String getColumn() {
        return column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }
}
