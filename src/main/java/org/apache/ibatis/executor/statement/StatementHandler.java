package org.apache.ibatis.executor.statement;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * </p>
 *封装SQL 执行相关的底层操
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public interface StatementHandler {

    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    void parameterize(Statement statement) throws SQLException;

    int update(Statement statement) throws SQLException;

    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

    BoundSql getBoundSql();

    ParameterHandler getParameterHandler();
}
