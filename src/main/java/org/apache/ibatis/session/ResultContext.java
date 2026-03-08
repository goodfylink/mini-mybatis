package org.apache.ibatis.session;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public interface ResultContext<T> {
    T getResultObject();

    int getResultCount();

    boolean isStopped();

    void stop();
}
