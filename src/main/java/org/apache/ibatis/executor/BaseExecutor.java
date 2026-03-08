package org.apache.ibatis.executor;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * </p>
 * Executor 接口的抽象基类
 * 
 * @author jcyin
 * @since 2026/2/14
 *        </p>
 */
public abstract class BaseExecutor implements Executor {
    // 绑定的事务对象
    protected Transaction transaction;
    // 装饰器包装后的执行器
    protected Executor wrapper;
    // 全局配置文件
    protected Configuration configuration;
    // 执行器关闭状态标记
    private boolean closed;

    public BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.closed = false;
        this.wrapper = this;
    }

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        return transaction;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        return doUpdate(ms, parameter);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        BoundSql boundSql = ms.getBoundSql(parameter);
        return doQuery(ms, parameter, boundSql);
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                if (transaction != null) {
                    transaction.close();
                }
            }
        } catch (SQLException e) {
            throw new ExecutorException("close false with" + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }

    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new ExecutorException("Cannot commit, transaction is already closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, BoundSql boundSql) throws SQLException;

    @Override
    public void setExecutorWrapper(Executor wrapper) {
        this.wrapper = wrapper;
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                //
            }
        }
    }

    protected Connection getConnection() throws SQLException {
        Connection connection = transaction.getConnection();
        if (connection == null) {
            throw new SQLException("Connection has not been closed");
        }
        return connection;
    }
}
