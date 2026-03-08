package org.apache.ibatis.executor.statement;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;

import java.sql.*;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public class PreparedStatementHandler extends BaseStatementHandler {

    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter,
                                    ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter,resultHandler, boundSql);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return ps.getUpdateCount();
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.handleResultSets(ps);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        return connection.prepareStatement(sql);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        parameterHandler.setParameters((PreparedStatement) statement);
    }
}
