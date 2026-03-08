package org.apache.ibatis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/20
 *        </p>
 */
public class MetaObject {
    private final Object originalObject;

    public MetaObject(Object originalObject) {
        this.originalObject = originalObject;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public void setValue(String propertyName, Object value) {
        if (propertyName == null || propertyName.isEmpty()) {
            return;
        }

        if (propertyName.contains(".")) {
            int lastDotIndex = propertyName.lastIndexOf(".");
            String parentPath = propertyName.substring(0, lastDotIndex);
            String lastProperty = propertyName.substring(lastDotIndex + 1);
            Object target = getValue(parentPath);
            if (target != null) {
                new MetaObject(target).setValue(lastProperty, value);
            }
        } else {
            setSimpleProperty(originalObject, propertyName, value);
        }
    }

    private void setSimpleProperty(Object obj, String propertyName, Object value) {
        if (obj instanceof java.util.Map) {
            ((java.util.Map) obj).put(propertyName, value);
            return;
        }
        Class<?> type = obj.getClass();
        // 先找 setter
        String setterName = "set" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        for (Method method : type.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterTypes().length == 1) {
                try {
                    method.invoke(obj, value);
                    return;
                } catch (Exception e) {
                    //
                }
            }
        }
        // 再试字段
        try {
            Field field = type.getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            //
        }
    }

    public Object getValue(String propertyName) {
        if (propertyName == null || propertyName.isEmpty()) {
            return null;
        }
        String[] parts = propertyName.split("\\.");
        Object current = originalObject;
        for (String part : parts) {
            if (current == null) {
                return null;
            }
            if (current instanceof java.util.Map) {
                current = ((java.util.Map<?, ?>) current).get(part);
            } else {
                current = getSimpleProperty(current, part);
            }
        }
        return current;
    }

    private Object getSimpleProperty(Object obj, String propertyName) {
        Class<?> type = obj.getClass();
        // 1. 尝试 getXxx 方法
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        try {
            Method m = type.getMethod(getterName);
            return m.invoke(obj);
        } catch (Exception e) {
            //
        }
        // 2. 尝试 isXxx（处理 boolean）
        getterName = "is" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        try {
            Method m = type.getMethod(getterName);
            return m.invoke(obj);
        } catch (Exception e) {
            //
        }
        // 3. 尝试同名字段
        try {
            Field field = type.getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            // 找不到就返回null
            return null;
        }
    }
}
