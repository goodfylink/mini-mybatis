package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public interface Transaction {

    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;

    Integer getTimeout() throws SQLException;

}
