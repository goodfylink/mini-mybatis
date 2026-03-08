package org.apache.ibatis.mapping;

import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/10
 * </p>
 */
public final class Environment {

    private final String id;
    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder {
        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;
        public Builder(String id) {
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public String id() {
            return this.id;
        }

        public Environment build() {
            return new Environment(this.id, this.transactionFactory, this.dataSource);
        }
    }
    public String getId() {
        return this.id;
    }

    public TransactionFactory getTransactionFactory() {
        return this.transactionFactory;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
