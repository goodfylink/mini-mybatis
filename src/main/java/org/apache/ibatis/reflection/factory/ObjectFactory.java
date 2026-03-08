package org.apache.ibatis.reflection.factory;

import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/20
 * </p>
 */
public interface ObjectFactory {
    <T> T create(Class<T> type);
    void setProperties(Properties properties);
}
