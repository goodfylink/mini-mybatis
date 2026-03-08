package org.apache.ibatis.transaction;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public class TransactionException extends RuntimeException {

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

}
