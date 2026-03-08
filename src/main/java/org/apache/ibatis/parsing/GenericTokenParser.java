package org.apache.ibatis.parsing;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/18
 * </p>
 */
public class GenericTokenParser {
    private final String openToken;
    private final String closeToken;
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int start = text.indexOf(openToken);
        if (start == -1) {
            return text;
        }
        char[] src = text.toCharArray();
        int offset = 0;
        StringBuilder builder = new StringBuilder();
        StringBuilder expression = new StringBuilder();
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // 转义：\${xxx} -> ${xxx}
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                if (end == -1) {
                    // 没有找到闭合符，剩下的原样输出
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    expression.setLength(0);
                    expression.append(src, offset, end - offset);
                    builder.append(handler.handleToken(expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

}
