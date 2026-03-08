package org.apache.ibatis.build;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;

import java.io.InputStream;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/19
 *        </p>
 */
public class XMLMapperBuilder extends BaseBuilder {
    private final XPathParser parser;
    private final String resource;
    private final MapperBuilderAssistant builderAssistant;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) {
        super(configuration);
        this.parser = new XPathParser(inputStream, false, configuration.getVariables(), null);
        this.resource = resource;
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
    }

    public void parse() {
        XNode mapperNode = parser.evalNode("/mapper");
        if (mapperNode == null) {
            throw new BuilderException("Mapper XML root <mapper> not found: " + resource);
        }
        String namespace = mapperNode.getStringAttribute("namespace");
        builderAssistant.setCurrentNamespace(namespace);
        for (XNode child : mapperNode.getChildren()) {
            String name = child.getName();
            if ("select".equals(name)) {
                buildStatementFromContext(child, SqlCommandType.SELECT);
            } else if ("insert".equals(name)) {
                buildStatementFromContext(child, SqlCommandType.INSERT);
            } else if ("update".equals(name)) {
                buildStatementFromContext(child, SqlCommandType.UPDATE);
            } else if ("delete".equals(name)) {
                buildStatementFromContext(child, SqlCommandType.DELETE);
            }
        }
        bindMapperForNamespace();
    }

    private void buildStatementFromContext(XNode context, SqlCommandType type) {
        String id = context.getStringAttribute("id");
        String parameterType = context.getStringAttribute("parameterType");
        String resultType = context.getStringAttribute("resultType");
        Class<?> parameterTypeClass = resolveClass(parameterType);
        Class<?> resultTypeClass = resolveClass(resultType);
        String sql = context.getStringBody().trim();
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        SqlSource sqlSource = sqlSourceParser.parse(sql, parameterTypeClass, null);
        builderAssistant.addMappedStatement(id, sqlSource, type, parameterTypeClass, resultTypeClass);
    }

    private void bindMapperForNamespace() {
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace == null) {
            return;
        }
        try {
            Class<?> boundType = Resources.classForName(namespace);
            if (boundType != null && !configuration.hasMapper(boundType)) {
                configuration.addMapper(boundType);
            }
        } catch (ClassNotFoundException e) {
            //
        }
    }
}
