package org.apache.ibatis.transaction;


import org.apache.ibatis.session.TransactionIsolationLevel;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;


/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public interface TransactionFactory {

    default void setProperties(Properties props) {
    }

    Transaction newTransaction(Connection conn);

    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);

}
