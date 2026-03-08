package org.apache.ibatis.executor.result;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/15
 * </p>
 */
public class ResultMapException extends RuntimeException {
    public ResultMapException(String message) {
        super(message);
    }

    public ResultMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultMapException(Throwable cause) {
        super(cause);
    }
}
