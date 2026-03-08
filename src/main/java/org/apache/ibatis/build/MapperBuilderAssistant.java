package org.apache.ibatis.build;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/19
 * </p>
 */
public class MapperBuilderAssistant extends BaseBuilder {

    private String currentNamespace;
    private final String resource;

    public MapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
    }

    public void setCurrentNamespace(String currentNamespace) {
        if (currentNamespace == null || currentNamespace.isEmpty()) {
            throw new BuilderException("Current namespace cannot be null or empty");
        }
        if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
            throw new BuilderException("Wrong namespace. Expected '" + this.currentNamespace
                    + "' but found '" + currentNamespace + "' in '" + resource + "'");
        }
        this.currentNamespace = currentNamespace;
    }

    public String applyCurrentNamespace(String id, boolean isReference) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        if (currentNamespace == null){
            return id;
        }
        if (isReference && id.contains(".")) {
            // 引用且已有命名空间，认为是全限定名
            return id;
        }
        return currentNamespace + "." + id;
    }

    public MappedStatement addMappedStatement(
            String id,
            SqlSource sqlSource,
            SqlCommandType sqlCommandType,
            Class<?> parameterType,
            Class<?> resultType) {
        if (id == null || id.isEmpty()) {
            throw new BuilderException("Mapped statement id is required in mapper '" + currentNamespace + "'");
        }

        String statementId = applyCurrentNamespace(id, false);

        MappedStatement.Builder builder =
                new MappedStatement.Builder(configuration, statementId, sqlSource, sqlCommandType);

        if (resultType != null) {
            builder.resultType(resultType);
        }

        MappedStatement ms = builder.build();
        configuration.addMappedStatement(ms);
        return ms;
    }

        public String getCurrentNamespace() {
        return currentNamespace;
    }
}
