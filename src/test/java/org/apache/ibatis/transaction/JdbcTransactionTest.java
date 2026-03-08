package org.apache.ibatis.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 *        </p>
 */
public class JdbcTransactionTest {

    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement();) {
            stmt.execute("DROP TABLE IF EXISTS t_user");
            stmt.execute("CREATE TABLE t_user (id INT PRIMARY KEY, name VARCHAR(50))");
        }
    }

    @Test
    public void testOpenConnectionAndConfig() throws Exception {
        JdbcTransaction transaction = new JdbcTransaction(dataSource, TransactionIsolationLevel.REPEATABLE_READ,
                false);
        Connection connection = transaction.getConnection();
        assert connection != null;
        assertEquals(Connection.TRANSACTION_REPEATABLE_READ, connection.getTransactionIsolation());
        System.out.println(connection.getTransactionIsolation());
        assertFalse(connection.getAutoCommit());
        connection.close();
    }

    @Test
    public void testCommit() throws Exception {
        JdbcTransaction tx = new JdbcTransaction(dataSource, TransactionIsolationLevel.READ_COMMITTED, false);
        Connection conn = tx.getConnection();

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO t_user (id, name) VALUES (1, 'A')");
        }

        tx.commit();

        try (Connection other = dataSource.getConnection();
                Statement stmt = other.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_user WHERE id = 1")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }

        tx.close();
    }

    @Test
    public void testRollback() throws Exception {
        JdbcTransaction tx = new JdbcTransaction(dataSource, TransactionIsolationLevel.READ_COMMITTED, false);
        Connection conn = tx.getConnection();

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO t_user (id, name) VALUES (2, 'B')");
        }

        tx.rollback();
        // tx.commit();

        try (Connection other = dataSource.getConnection();
                Statement stmt = other.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_user WHERE id = 2")) {
            // System.out.println(rs.getString("name"));
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }

        tx.close();
    }
}
