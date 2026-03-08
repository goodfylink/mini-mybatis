package org.apache.ibatis.executor;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * </p>
 *MyBatis 执行 SQL 语句的顶层核心接口
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public interface Executor {
    //执行增，删 ，改操作
    int update(MappedStatement ms, Object parameter) throws SQLException;
    //执行查询操作
    <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException;
    //事务提交，required=true(强制提交)
    void commit(boolean required) throws SQLException;
    //事务回滚，required=true(强制回滚)
    void rollback(boolean required) throws SQLException;
    //关闭执行器
    void close(boolean forceRollback);
    //判断是否关闭
    boolean isClosed();
    //获取Transaction，自定义事务使用
    Transaction getTransaction();
    //装饰器模式装饰Executor
    void setExecutorWrapper(Executor executor);
}
