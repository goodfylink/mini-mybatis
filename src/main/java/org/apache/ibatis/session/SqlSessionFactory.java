package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/21
 * </p>
 */
public interface SqlSessionFactory {
    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(Connection connection);

    SqlSession openSession(TransactionIsolationLevel level);

    Configuration getConfiguration();
}
