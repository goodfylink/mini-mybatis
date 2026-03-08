package org.apache.ibatis.session;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.datasource.UnpooledDataSourceFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/6
 *        </p>
 */
public class Configuration {

    protected Environment environment;

    protected String databaseId;

    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    protected final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>(
            "Mapped Statements collection")
            .conflictMessageProducer((savedValue, targetValue) -> ". please check " +
                    savedValue.getId() + " -> " + targetValue.getId());

    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected Properties variables = new Properties();

    protected ObjectFactory objectFactory = new DefaultObjectFactory();

    protected JdbcType jdbcTypeForNull = JdbcType.forCode(Types.OTHER);

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);

    }

    public StatementHandler newStatementHandler(Executor executor,
            MappedStatement ms,
            Object parameterObject,
            BoundSql bs) {
        return new PreparedStatementHandler(executor, ms, parameterObject, null, bs);
    }

    public ParameterHandler newParameterHandler(MappedStatement ms,
            Object parameterObject,
            BoundSql boundSql) {
        return new DefaultParameterHandler(ms, parameterObject, boundSql);
    }

    public ResultSetHandler newResultSetHandler(Executor executor,
            MappedStatement ms,
            ResultHandler resultHandler,
            BoundSql boundSql) {
        return new DefaultResultSetHandler(this, executor, ms, resultHandler, boundSql);
    }

    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public MappedStatement getMappedStatement(String statement) {
        return mappedStatements.get(statement);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public Properties getVariables() {
        return variables;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public MetaObject newMetaObject(Object object) {
        return new MetaObject(object);
    }

    public class StrictMap<V> extends HashMap<String, V> {
        private final String name;
        private BiFunction<V, V, String> conflictMessageProducer;

        public StrictMap(String name) {
            super();
            this.name = name;
        }

        public StrictMap<V> conflictMessageProducer(BiFunction<V, V, String> conflictMessageProducer) {
            this.conflictMessageProducer = conflictMessageProducer;
            return this;
        }

        @Override
        public V put(String key, V value) {
            V existing = super.get(key);
            if (existing != null) {
                if (conflictMessageProducer != null) {
                    String extra = conflictMessageProducer.apply(existing, value);
                    throw new IllegalArgumentException(
                            name + " already contains value for '" + key + "'" + extra);
                } else {
                    throw new IllegalArgumentException(
                            name + " already contains value for '" + key + "'");
                }
            }
            return super.put(key, value);
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            return value;
        }
    }
}
