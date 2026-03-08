package org.apache.ibatis;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public class MybatisException extends Exception{
    public MybatisException(String message) {
        super(message);
    }
    public MybatisException(String message, Throwable cause) {
        super(message, cause);
    }
    public MybatisException(Throwable cause) {
        super(cause);
    }
    public MybatisException(){
        super();
    }
}
