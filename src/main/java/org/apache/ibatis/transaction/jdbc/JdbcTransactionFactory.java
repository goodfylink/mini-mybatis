package org.apache.ibatis.transaction.jdbc;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public class JdbcTransactionFactory implements TransactionFactory {

    private boolean skipSetAutoCommitOnClose;

    @Override
    public void setProperties(Properties props) {
        if (props == null) {
            return;
        }
        String value = props.getProperty("skipSetAutoCommitOnClose");
        if (value != null) {
            this.skipSetAutoCommitOnClose = Boolean.parseBoolean(value);
        }
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit, skipSetAutoCommitOnClose);
    }

}
