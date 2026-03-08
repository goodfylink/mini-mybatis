package org.apache.ibatis.session.defaults;

import org.apache.ibatis.MybatisException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(null, false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSessionFromDataSource(null, autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return openSessionFromConnection(connection);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return openSessionFromDataSource(level, false);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    protected SqlSession createSqlSession(Configuration configuration, Executor executor) {
        return new DefaultSqlSession(configuration, executor);
    }

    private SqlSession openSessionFromDataSource(TransactionIsolationLevel level, boolean autoCommit) {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            final Executor executor = configuration.newExecutor(tx);
            return createSqlSession(configuration, executor);
        } catch (Exception e) {
            closeTransaction(tx);
            throw new ResultException("Error opening session.  Cause: " + e, e);
        }
    }

    private SqlSession openSessionFromConnection(Connection connection) {
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            final Transaction tx = transactionFactory.newTransaction(connection);
            final Executor executor = configuration.newExecutor(tx);
            return createSqlSession(configuration, executor);
        } catch (Exception e) {
            throw new ResultException("Error opening session.  Cause: " + e, e);
        }
    }

    private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
        return environment.getTransactionFactory();
    }

    private void closeTransaction(Transaction tx) {
        if (tx != null) {
            try {
                tx.close();
            } catch (SQLException ignore) {
                //
            }
        }
    }
}
