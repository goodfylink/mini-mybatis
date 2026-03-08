package org.apache.ibatis.executor.resultset;

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
public interface ResultSetHandler {
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;
}
