package org.apache.ibatis.session;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/16
 * </p>
 */
public class ResultException extends RuntimeException {
    public ResultException(String message) {
        super(message);
    }
    public ResultException(String message, Throwable cause) {
      super(message, cause);
    }
    public ResultException(Throwable cause) {
      super(cause);
    }
}
