package org.apache.ibatis.session.defaults;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultException;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;

    private final Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = this.selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() > 1) {
            throw new ResultException("Multiple results found for statement: " + statement);
        }else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return this.selectList(statement, null);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.query(ms,parameter);
        }catch (Exception e){
            throw new ResultException("Failed to execute statement '" + statement + "' with parameter: " + parameter, e);
        }
    }

    @Override
    public int insert(String statement) {
        return this.insert(statement, null);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return update(statement, null);
    }

    @Override
    public int update(String statement, Object parameter) {
        try{
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.update(ms,wrapCollection(parameter));
        }catch (Exception e){
            throw new ResultException("Failed to execute statement '" + statement + "' with parameter: " + parameter, e);
        }
    }

    @Override
    public int delete(String statement) {
        return delete(statement, null);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public void commit() {
        this.commit(false);
    }

    @Override
    public void commit(boolean force) {
        try {
            executor.commit(force);
        }catch (Exception e){
            throw new ResultException("failed to commit", e);
        }
    }

    @Override
    public void rollback() {
        this.rollback(false);
    }

    @Override
    public void rollback(boolean force) {
        try{
            executor.commit(force);
        }catch (Exception e){
            throw new ResultException("failed to rollback", e);
        }
    }

    @Override
    public void close() {
        try {
            executor.close(false);
        }catch (Exception e){
            throw new ResultException("failed to close", e);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Connection getConnection() {
        try{
            return executor.getTransaction().getConnection();
        }catch (Exception e){
            throw new ResultException("failed to get connection", e);
        }
    }

    private Object wrapCollection(final Object object) {
        return ParamNameResolver.wrapToMapIfCollection(object);
    }
}
