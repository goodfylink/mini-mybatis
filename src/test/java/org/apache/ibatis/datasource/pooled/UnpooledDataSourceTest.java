package org.apache.ibatis.datasource.pooled;

import org.junit.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Test cases for UnpooledDataSource class
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class UnpooledDataSourceTest {

    @Test
    public void testGetConnection() throws SQLException {
        UnpooledDataSource ds = new UnpooledDataSource();
        ds.setDriver("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:dstest;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");

        try (Connection conn = ds.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.getAutoCommit());
        }
    }

    @Test
    public void testProperties() {
        UnpooledDataSource ds = new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:dstest", "sa", "pass");
        assertEquals("org.h2.Driver", ds.getDriver());
        assertEquals("jdbc:h2:mem:dstest", ds.getUrl());
        assertEquals("sa", ds.getUsername());
        assertEquals("pass", ds.getPassword());
    }
}
