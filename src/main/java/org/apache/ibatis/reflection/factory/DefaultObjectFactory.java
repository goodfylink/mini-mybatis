package org.apache.ibatis.reflection.factory;

import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/20
 * </p>
 */
public class DefaultObjectFactory implements ObjectFactory {
    @Override
    public <T> T create(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance for " + type, e);
        }
    }

    @Override
    public void setProperties(Properties properties) {
        //
    }

}
