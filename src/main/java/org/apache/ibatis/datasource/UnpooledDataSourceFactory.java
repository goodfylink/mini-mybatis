package org.apache.ibatis.datasource;

import org.apache.ibatis.datasource.pooled.UnpooledDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {
    private static final String DRIVER = "driver";
    private static final String URL = "url";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static final String DRIVER_PROPERTY_PREFIX = "driver."; // driver.xxx

    private final UnpooledDataSource dataSource;

    public UnpooledDataSourceFactory() {
        this.dataSource = new UnpooledDataSource();
    }

    @Override
    public void setProperties(Properties props) {
//        // 1. 解析 driver.xxx 作为 JDBC 驱动属性（可选）
//        Properties driverProps = new Properties();
//        for (String name : props.stringPropertyNames()) {
//            if (name.startsWith(DRIVER_PROPERTY_PREFIX)) {
//                String key = name.substring(DRIVER_PROPERTY_PREFIX.length());
//                String value = props.getProperty(name);
//                driverProps.setProperty(key, value);
//            }
//        }
//        if (!driverProps.isEmpty()) {
//            dataSource.setDriverProperties(driverProps); // 如果你有这个方法
//        }

        // 2. 基本连接信息
        String driver = props.getProperty(DRIVER);
        if (driver != null && !driver.isEmpty()) {
            dataSource.setDriver(driver);
        }

        String url = props.getProperty(URL);
        if (url != null && !url.isEmpty()) {
            dataSource.setUrl(url);
        }

        String username = props.getProperty(USERNAME);
        if (username != null && !username.isEmpty()) {
            dataSource.setUsername(username);
        }

        String password = props.getProperty(PASSWORD);
        if (password != null) { // 允许空密码
            dataSource.setPassword(password);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
