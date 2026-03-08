package org.apache.ibatis.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/19
 * </p>
 */
public final class TypeParameterResolver {
    private TypeParameterResolver() {
    }

    public static Type resolveReturnType(Method method, Class<?> mapperInterface) {
        // 精简版：不做复杂泛型解析，直接使用方法声明的泛型返回类型
        return method.getGenericReturnType();
    }
}
