package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/16
 * </p>
 */
public class ParameterMap {
    private String id;
    private Class<?> type;
    private List<ParameterMapping> parameterMappings;

    private ParameterMap() {
    }

    public static class Builder {

        private final ParameterMap parameterMap = new ParameterMap();


        public Builder(Configuration configuration, String id, Class<?> type, List<ParameterMapping> parameterMappings) {
            parameterMap.id = id;
            parameterMap.type = type;
            parameterMap.parameterMappings = parameterMappings;
        }

        public Class<?> type() {
            return parameterMap.type;
        }

        public ParameterMap build() {
            // lock down collections
            parameterMap.parameterMappings = Collections.unmodifiableList(parameterMap.parameterMappings);
            return parameterMap;
        }
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Class<?> getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
