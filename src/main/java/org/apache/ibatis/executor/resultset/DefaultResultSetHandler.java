package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultResultSetHandler implements ResultSetHandler {

    private final Configuration configuration;
    private final MappedStatement mappedStatement;
    private final ResultHandler resultHandler;
    private final BoundSql boundSql;

    private final ObjectFactory objectFactory;
    private final TypeHandlerRegistry typeHandlerRegistry;

    public DefaultResultSetHandler(Configuration configuration,
                                   Executor executor,
                                   MappedStatement mappedStatement,
                                   ResultHandler resultHandler,
                                   BoundSql boundSql) {
        this.configuration = configuration;
        this.mappedStatement = mappedStatement;
        this.resultHandler = resultHandler;
        this.boundSql = boundSql;

        this.objectFactory = configuration.getObjectFactory();
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        List<E> results = new ArrayList<>();

        ResultSet rs = stmt.getResultSet();
        if (rs == null) {
            return results;
        }

        ResultMap resultMap = resolveResultMap();
        Class<?> resultType = resultMap.getType();

        // 1) 简单类型：直接按第一列读取
        if (typeHandlerRegistry.hasTypeHandler(resultType)) {
            @SuppressWarnings("unchecked")
            TypeHandler<Object> typeHandler =
                    (TypeHandler<Object>) typeHandlerRegistry.getTypeHandler(resultType);
            while (rs.next()) {
                Object value = typeHandler.getResult(rs, 1);
                @SuppressWarnings("unchecked")
                E row = (E) value;
                results.add(row);
            }
            return results;
        }

        // 2) Map 类型
        if (Map.class.isAssignableFrom(resultType)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = rsmd.getColumnLabel(i);
                    if (columnLabel == null || columnLabel.isEmpty()) {
                        columnLabel = rsmd.getColumnName(i);
                    }
                    Object value = rs.getObject(i);
                    row.put(columnLabel, value);
                }
                @SuppressWarnings("unchecked")
                E rowE = (E) row;
                results.add(rowE);
            }
            return results;
        }

        // 3) JavaBean：按 ResultMap 的 ResultMapping 做映射
        List<ResultMapping> mappings = resultMap.getResultMappings();
        if (mappings == null || mappings.isEmpty()) {
            autoMapByColumnName(results, rs, resultType);
        } else {
            mapByResultMap(results, rs, resultType, mappings);
        }
        return results;
    }

    private ResultMap resolveResultMap() {
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        if (resultMaps != null && !resultMaps.isEmpty()) {
            return resultMaps.get(0);
        }
        // 没配置 resultMap 时默认 Map
        return new ResultMap.Builder(configuration,
                "defaultResultMap-" + mappedStatement.getId(),
                Map.class,
                new ArrayList<>()
        ).build();
    }

    private <E> void mapByResultMap(List<E> results,
                                    ResultSet rs,
                                    Class<?> resultType,
                                    List<ResultMapping> mappings) throws SQLException {
        while (rs.next()) {
            @SuppressWarnings("unchecked")
            E rowObj = (E) objectFactory.create(resultType);
            MetaObject metaObject = configuration.newMetaObject(rowObj);

            for (ResultMapping mapping : mappings) {
                String property = mapping.getProperty();
                String column = mapping.getColumn();
                if (property == null || column == null) {
                    continue;
                }

                TypeHandler<?> th = mapping.getTypeHandler();
                if (th == null) {
                    Class<?> javaType = mapping.getJavaType();
                    JdbcType jdbcType = mapping.getJdbcType();
                    th = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
                    if (th == null) {
                        continue;
                    }
                }

                @SuppressWarnings("unchecked")
                TypeHandler<Object> typeHandler = (TypeHandler<Object>) th;
                Object value = typeHandler.getResult(rs, column);

                try {
                    metaObject.setValue(property, value);
                } catch (Exception ignore) {
                }
            }

            results.add(rowObj);
        }
    }

    private <E> void autoMapByColumnName(List<E> results,
                                         ResultSet rs,
                                         Class<?> resultType) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        while (rs.next()) {
            @SuppressWarnings("unchecked")
            E rowObj = (E) objectFactory.create(resultType);
            MetaObject metaObject = configuration.newMetaObject(rowObj);

            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                if (columnLabel == null || columnLabel.isEmpty()) {
                    columnLabel = rsmd.getColumnName(i);
                }
                // 将列名转换为小写，以匹配Java属性名
                String propertyName = columnLabel.toLowerCase();
                
                // 尝试通过反射获取属性类型，以便使用正确的TypeHandler
                Class<?> propertyType = getPropertyType(resultType, propertyName);
                Object value;
                if (propertyType != null && typeHandlerRegistry.hasTypeHandler(propertyType)) {
                    // 使用TypeHandler进行类型转换
                    @SuppressWarnings("unchecked")
                    TypeHandler<Object> typeHandler = (TypeHandler<Object>) typeHandlerRegistry.getTypeHandler(propertyType);
                    value = typeHandler.getResult(rs, columnLabel);
                } else {
                    // 如果找不到TypeHandler，直接使用getObject
                    value = rs.getObject(i);
                }
                
                try {
                    metaObject.setValue(propertyName, value);
                } catch (Exception ignore) {
                    // 如果小写失败，尝试使用原始列名
                    try {
                        metaObject.setValue(columnLabel, value);
                    } catch (Exception ignore2) {
                    }
                }
            }

            results.add(rowObj);
        }
    }
    
    /**
     * 通过反射获取JavaBean属性的类型
     */
    private Class<?> getPropertyType(Class<?> clazz, String propertyName) {
        if (propertyName == null || propertyName.isEmpty()) {
            return null;
        }
        
        // 尝试通过getter方法获取类型
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        try {
            java.lang.reflect.Method method = clazz.getMethod(getterName);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            // 尝试isXxx方法（用于boolean类型）
            getterName = "is" + Character.toUpperCase(propertyName.charAt(0))
                    + propertyName.substring(1);
            try {
                java.lang.reflect.Method method = clazz.getMethod(getterName);
                return method.getReturnType();
            } catch (NoSuchMethodException e2) {
                // 尝试直接访问字段
                try {
                    java.lang.reflect.Field field = clazz.getDeclaredField(propertyName);
                    return field.getType();
                } catch (NoSuchFieldException e3) {
                    return null;
                }
            }
        }
    }
}