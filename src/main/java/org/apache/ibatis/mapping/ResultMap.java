package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public class ResultMap {

    private final String id;
    private final Class<?> type;                  // 结果对象类型
    private final List<ResultMapping> resultMappings;

    private ResultMap(String id, Class<?> type, List<ResultMapping> resultMappings) {
        this.id = id;
        this.type = type;
        this.resultMappings = Collections.unmodifiableList(resultMappings);
    }

    public static class Builder {
        private final Configuration configuration;  // 先保留字段，以后需要可以用
        private final String id;
        private final Class<?> type;
        private final List<ResultMapping> resultMappings = new ArrayList<>();

        public Builder(Configuration configuration,
                       String id,
                       Class<?> type,
                       List<ResultMapping> mappings) {
            this.configuration = configuration;
            this.id = id;
            this.type = type;
            if (mappings != null) {
                this.resultMappings.addAll(mappings);
            }
        }

        public Builder addResultMapping(ResultMapping mapping) {
            this.resultMappings.add(mapping);
            return this;
        }

        public Class<?> type() {
            return type;
        }

        public ResultMap build() {
            if (id == null) {
                throw new IllegalArgumentException("ResultMap must have an id");
            }
            return new ResultMap(id, type, resultMappings);
        }
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }
}
