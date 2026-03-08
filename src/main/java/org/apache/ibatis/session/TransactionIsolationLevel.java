package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public enum TransactionIsolationLevel {

    NONE(Connection.TRANSACTION_NONE),

    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),

    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),

    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),

    SQL_SERVER_SNAPSHOT(0x1000);

    private final int level;

    TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
