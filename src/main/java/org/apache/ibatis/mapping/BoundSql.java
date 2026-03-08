package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * </p>
 *'解析后的动态sql
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public class BoundSql {
    //解析后的最终静态 SQL
    private final String sql;
    //#{} 占位符对应的参数映射列表
    private final List<ParameterMapping> parameterMappings;
    //最终的参数值对象
    private final Object parameterObject;

    public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings,
                    Object parameterObject) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterObject = parameterObject;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public String getSql() {
        return sql;
    }
}
