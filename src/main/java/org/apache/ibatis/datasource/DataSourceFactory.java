package org.apache.ibatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
public interface DataSourceFactory {
    void setProperties(Properties props);

    DataSource getDataSource();

}
