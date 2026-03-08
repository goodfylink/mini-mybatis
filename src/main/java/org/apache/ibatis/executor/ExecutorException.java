package org.apache.ibatis.executor;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public class ExecutorException extends RuntimeException {
    public ExecutorException(String message) {
        super(message);
    }

    public ExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
