package org.apache.ibatis.datasource.pooled;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
@SuppressWarnings("all")
public class UnpooledDataSource implements DataSource {
    private ClassLoader driverClassLoader;

    private String driver;
    private String url;
    private String username;
    private String password;

    public UnpooledDataSource() {
    }

    public UnpooledDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        throw new SQLException("UnpooledDataSource does not implement " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return doGetConnection(props);
    }

    private Connection doGetConnection(Properties properties) throws SQLException {
        initializeDriver();
        Connection connection = DriverManager.getConnection(url, properties);
        configureConnection(connection);
        return connection;
    }

    private void initializeDriver() throws SQLException {

        Class<?> driverClass;
        try {
            ClassLoader cl = driverClassLoader != null ? driverClassLoader : Thread.currentThread().getContextClassLoader();
            driverClass = Class.forName(driver, true, cl);

            Driver driverInstance = (Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(new DriverProxy(driverInstance));

        } catch (Exception e) {
            throw new SQLException("Failed to initialize driver class: " + driver, e);
        }
    }

    private void configureConnection(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
    }

    private static class DriverProxy implements Driver {
        private final Driver driver;

        public DriverProxy(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public String getDriver() {
        return driver;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}



