package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public final class MappedStatement {

    private String id;

    private Configuration configuration;

    private SqlSource sqlSource;

    private SqlCommandType sqlCommandType;

    private ParameterMap parameterMap;

    private List<ResultMap> resultMaps;

    MappedStatement() {

    }

    public static class Builder {
        private final MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.parameterMap = new ParameterMap.Builder(configuration, "defaultParameterMap", null, new ArrayList<>()).build();
            mappedStatement.resultMaps = new ArrayList<>();
        }
        public Builder resultType(Class<?> resultType) {
            if (resultType == null) {
                return this;
            }
            ResultMap.Builder rmBuilder =
                    new ResultMap.Builder(mappedStatement.configuration,
                            "defaultResultMap",
                            resultType,
                            new ArrayList<>());
            mappedStatement.resultMaps = Collections.singletonList(rmBuilder.build());
            return this;
        }
        public MappedStatement build() {
            return mappedStatement;
        }
    }

    public String getId() {
        return this.id;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public SqlSource getSqlSource() {
        return this.sqlSource;
    }

    public SqlCommandType getSqlCommandType() {
        return this.sqlCommandType;
    }

    public List<ResultMap> getResultMaps() {
        return this.resultMaps;
    }

    public ParameterMap getParameterMap() {
        return this.parameterMap;
    }


    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            boundSql = new BoundSql(configuration, boundSql.getSql(), parameterMap.getParameterMappings(), parameterObject);
        }
        return boundSql;
    }
}
