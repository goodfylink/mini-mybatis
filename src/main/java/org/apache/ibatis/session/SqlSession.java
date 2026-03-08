package org.apache.ibatis.session;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public interface SqlSession extends Closeable {

    <T> T selectOne(String statement);

    <T> T selectOne(String statement, Object parameter);

    <E> List<E> selectList(String statement);

    <E> List<E> selectList(String statement, Object parameter);

    int insert(String statement);

    int insert(String statement, Object parameter);

    int update(String statement);

    int update(String statement, Object parameter);

    int delete(String statement);

    int delete(String statement, Object parameter);

    void commit();

    void commit(boolean force);

    void rollback();

    void rollback(boolean force);

    Configuration getConfiguration();

    @Override
    void close();

    <T> T getMapper(Class<T> type);

    Connection getConnection();
}
