package org.apache.ibatis.session;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public interface ResultHandler<T> {
    void handleResult(ResultContext<? extends T> resultContext);
}
