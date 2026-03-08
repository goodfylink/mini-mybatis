package org.apache.ibatis.mapping;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/19
 * </p>
 */
public interface SqlSource {
    BoundSql getBoundSql(Object parameterObject);
}
