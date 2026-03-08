package org.apache.ibatis.type;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public class TypeException extends RuntimeException {
    public TypeException(String message) {
        super(message);
    }
    public TypeException(String message, Throwable cause) {
        super(message, cause);
    }
    public TypeException(Throwable cause) {
        super(cause);
    }
    public TypeException(){}
}
