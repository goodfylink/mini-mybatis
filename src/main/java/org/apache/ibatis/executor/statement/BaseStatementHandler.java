package org.apache.ibatis.executor.statement;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public abstract class BaseStatementHandler implements StatementHandler {
    protected final Configuration configuration;
    protected final ObjectFactory objectFactory;
    protected final TypeHandlerRegistry typeHandlerRegistry;
    protected final ResultSetHandler resultSetHandler;
    protected final ParameterHandler parameterHandler;

    protected final Executor executor;
    protected final MappedStatement mappedStatement;
    protected BoundSql boundSql;

    protected BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject,
                                   ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;

        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.objectFactory = configuration.getObjectFactory();

        if (boundSql == null) {
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }
        this.boundSql = boundSql;


        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement,
                resultHandler, boundSql);
    }

    @Override
    public BoundSql getBoundSql() {
        return boundSql;
    }

    @Override
    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        Statement stmt = null;
        try {
            stmt = instantiateStatement(connection);
            setStatementTimeout(stmt, transactionTimeout);
            return stmt;
        } catch (SQLException e) {
            closeStatement(stmt);
            throw e;
        } catch (Exception e) {
            closeStatement(stmt);
            throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
        }
    }

    public abstract <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    protected void setStatementTimeout(Statement statement, Integer transactionTimeout) throws SQLException {
        if (transactionTimeout != null && transactionTimeout > 0) {
            statement.setQueryTimeout(transactionTimeout);
        }
    }

    private void closeStatement(Statement statement) throws SQLException {

        try {
            if (statement != null) {
                statement.close();
            }
        }catch (Exception e) {
            //
        }
    }
}
