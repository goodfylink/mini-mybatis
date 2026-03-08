package org.apache.ibatis.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;


import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public class JdbcTransactionFactoryTest {

    @Test
    public void testSetProperties() {
        JdbcTransactionFactory factory = new JdbcTransactionFactory();
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
        ds.setUser("sa");
        ds.setPassword("");
        Properties props = new Properties();
        props.setProperty("skipSetAutoCommitOnClose", "true");
        factory.setProperties(props);

        Transaction tx = factory.newTransaction(ds, TransactionIsolationLevel.READ_COMMITTED, false);
        assertTrue(tx instanceof JdbcTransaction);

        JdbcTransaction jdbcTx = (JdbcTransaction) tx;
        assertTrue(jdbcTx.isSkipSetAutoCommitOnClose());
    }
}
