package org.apache.ibatis.transaction.jdbc;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */

@SuppressWarnings("all")
public class JdbcTransaction implements Transaction {

    protected Connection connection;

    protected DataSource dataSource;

    protected TransactionIsolationLevel level;

    protected boolean autoCommit;

    protected boolean skipSetAutoCommitOnClose;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
        this.skipSetAutoCommitOnClose = false;
    }

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level,
                           boolean autoCommit, boolean skipSetAutoCommitOnClose) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
        this.skipSetAutoCommitOnClose = skipSetAutoCommitOnClose;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            try {
                resetAutoCommit();
            }finally {
                connection.close();
            }
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }

    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        if (level != null) {
            connection.setTransactionIsolation(level.getLevel());
        }
        setDesiredAutoCommit(autoCommit);
    }

    protected void resetAutoCommit() {
        try {
            if (!skipSetAutoCommitOnClose && !connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        }catch (SQLException e) {
            throw new TransactionException(
                    "Error resetting autoCommit to true. Cause: " + e, e);
        }
    }

    protected void setDesiredAutoCommit(boolean desiredAutoCommit)  {
        try {
            if (connection.getAutoCommit() != desiredAutoCommit){
                connection.setAutoCommit(desiredAutoCommit);
            }
        }catch (SQLException e) {
            throw new TransactionException(
                    "Error configuring AutoCommit.  " + "Your driver may not support getAutoCommit() or setAutoCommit(). "
                            + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e,
                    e);
        }
    }

    public boolean isSkipSetAutoCommitOnClose() {
        return skipSetAutoCommitOnClose;
    }
}
