package org.apache.ibatis.build;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;

import java.util.Locale;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 *        </p>
 */
public class XMLStatementBuilder extends BaseBuilder {
    private final XNode context;
    private final String namespace;

    public XMLStatementBuilder(Configuration configuration, XNode context, String namespace) {
        super(configuration);
        this.context = context;
        this.namespace = namespace;
    }

    public MappedStatement parseStatement() {
        String id = context.getStringAttribute("id");
        if (id == null || id.isEmpty()) {
            throw new BuilderException("Mapped statement is missing 'id' in mapper namespace " + namespace);
        }
        String statementId = namespace + "." + id;
        String nodeName = context.getName(); // select / insert / update / delete
        SqlCommandType sqlCommandType = resolveSqlCommandType(nodeName);
        String parameterTypeStr = context.getStringAttribute("parameterType");
        String resultTypeStr = context.getStringAttribute("resultType");

        Class<?> parameterType = null;
        Class<?> resultType = null;
        try {
            if (parameterTypeStr != null && !parameterTypeStr.isEmpty()) {
                parameterType = resolveClass(parameterTypeStr);
            }
            if (resultTypeStr != null && !resultTypeStr.isEmpty()) {
                resultType = resolveClass(resultTypeStr);
            }
        } catch (Exception e) {
            throw new BuilderException("Error resolving parameterType/resultType. Cause: " + e, e);
        }
        String sql = context.getStringBody().trim();
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        SqlSource sqlSource = sqlSourceParser.parse(sql, parameterType, null);
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statementId, sqlSource,
                sqlCommandType);

        if (resultType != null) {
            builder.resultType(resultType);
        }
        return builder.build();
    }

    private SqlCommandType resolveSqlCommandType(String nodeName) {
        String name = nodeName.toLowerCase(Locale.ENGLISH);
        if ("SELECT".equals(name)) {
            return SqlCommandType.SELECT;
        } else if ("INSERT".equals(name)) {
            return SqlCommandType.INSERT;
        } else if ("UPDATE".equals(name)) {
            return SqlCommandType.UPDATE;
        } else if ("DELETE".equals(name)) {
            return SqlCommandType.DELETE;
        }
        throw new BuilderException("Unknown statement type: " + nodeName);
    }
}
