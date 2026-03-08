package org.apache.ibatis.reflection;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public class ParamNameResolver {
    public static final String GENERIC_NAME_PREFIX = "param";

    public static final String[] GENERIC_NAME_CACHE = new String[10];

    static {
        for (int i = 0; i < 10; i++) {
            GENERIC_NAME_CACHE[i] = GENERIC_NAME_PREFIX + (i + 1);
        }
    }

    private final SortedMap<Integer, String> names;

    private boolean hasParamAnnotation;

    public ParamNameResolver(Method method) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<>();
        int paramCount = parameterAnnotations.length;
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            String name = null;
            for (Annotation annotation : parameterAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }
            if (name == null) {
                name = String.valueOf(map.size());
            }
            map.put(paramIndex, name);
        }
        names = Collections.unmodifiableSortedMap(map);
    }
    @SuppressWarnings("unused")
    public String[] getNames() {
        return names.values().toArray(new String[0]);
    }

    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (paramCount == 0 || args == null) {
            return null;
        }
        if (!hasParamAnnotation && paramCount == 1) {
            Object value = args[names.firstKey()];
            return wrapToMapIfCollection(value);
        }else {
            final Map<String,Object> params = new ParamMap<>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                params.put(entry.getValue(),args[entry.getKey()]);
                final String genericParamName = i < 10 ? GENERIC_NAME_CACHE[i] : GENERIC_NAME_PREFIX + (i + 1);
                if (!names.containsValue(genericParamName)) {
                    params.put(genericParamName,args[entry.getKey()]);
                }
                i++;
            }
            return params;
        }
    }
    public static Object wrapToMapIfCollection(Object object) {
        if (object instanceof Collection) {
            MapperMethod.ParamMap<Object> map = new MapperMethod.ParamMap<>();
            map.put("collection", object);
            if (object instanceof List) {
                map.put("list", object);
            }
            return map;
        }
        if (object != null && object.getClass().isArray()) {
            MapperMethod.ParamMap<Object> map = new MapperMethod.ParamMap<>();
            map.put("array", object);
            return map;
        }
        return object;
    }
}
