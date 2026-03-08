package org.apache.ibatis.parsing;

import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/18
 * </p>
 */
public class PropertyParser {

    private PropertyParser() {}

    public static String parse(String string, Properties properties) {
        if (string == null || string.isEmpty() || properties == null || properties.isEmpty()) {
            return string;
        }
        VariableTokenHandler handler = new VariableTokenHandler(properties);
        GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
        return parser.parse(string);
    }
    private static class VariableTokenHandler implements TokenHandler {
        private final Properties variables;

        private VariableTokenHandler(Properties variables) {
            this.variables = variables;
        }
        @Override
        public String handleToken(String content) {
            if (variables == null) {
                return "${" + content + "}";
            }
            if (variables.containsKey(content)) {
                return variables.getProperty(content);
            }
            // 不存在该 key，原样保留占位符
            return "${" + content + "}";
        }
    }
}
