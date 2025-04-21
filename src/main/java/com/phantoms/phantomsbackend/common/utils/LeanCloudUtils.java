package com.phantoms.phantomsbackend.common.utils;

import cn.leancloud.LCACL;
import cn.leancloud.LCException;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCNull;
import io.reactivex.Observable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LeanCloudUtils {

    // Store key-value pairs with ACL
    public static boolean storeKV(String key, String value, String userId) {
        LCObject kvObject = new LCObject("KVStore");
        kvObject.put("key", key);
        kvObject.put("value", value);

//        LCACL acl = new LCACL();
//        acl.setReadAccess(userId, true);
//        acl.setWriteAccess(userId, true);
//        kvObject.setACL(acl);

        Observable<? extends LCObject> observable = kvObject.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Storage failed: " + e.getMessage());
            return false;
        }
    }

    // Retrieve key-value pairs with permission check
    public static String getKV(String key, String userId) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
//                LCACL acl = kvObject.getACL();
//                if (acl != null && acl.getReadAccess(userId)) {
                    return kvObject.getString("value");
//                } else {
//                    System.out.println("No permission to read this object.");
//                    return null;
//                }
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Retrieval failed: " + e.getMessage());
            return null;
        }
    }

    // Update key-value pairs with permission check
    public static boolean updateKV(String key, String newValue, String userId) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
//                LCACL acl = kvObject.getACL();
//                if (acl != null && acl.getWriteAccess(userId)) {
                    kvObject.put("value", newValue);
                    Observable<? extends LCObject> updateObservable = kvObject.saveInBackground();
                    updateObservable.blockingFirst();
                    return true;
//                } else {
//                    System.out.println("No permission to update this object.");
//                    return false;
//                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    // Delete key-value pairs with permission check
    public static boolean deleteKV(String key, String userId) {
        LCQuery<LCObject> query = new LCQuery<>("KVStore");
        query.whereEqualTo("key", key);
        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            if (!results.isEmpty()) {
                LCObject kvObject = results.get(0);
//                LCACL acl = kvObject.getACL();
//                if (acl != null && acl.getWriteAccess(userId)) {
                    Observable<LCNull> deleteObservable = kvObject.deleteInBackground();
                    deleteObservable.blockingFirst();
                    return true;
//                } else {
//                    System.out.println("No permission to delete this object.");
//                    return false;
//                }
            } else {
                System.out.println("No record found for key: " + key);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Deletion failed: " + e.getMessage());
            return false;
        }
    }



    // 泛型方法：创建对象
    public static <T> boolean createObject(String className, T object, String userId) {
        LCObject lcObject = new LCObject(className);

        // 获取目标对象
        Object targetObject = getTargetObject(object);
        // 检查目标对象是否是 Map
        if (targetObject instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) targetObject;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (isLeanCloudSupportedType(value)) {
                    lcObject.put(key.toString(), value);
                } else {
                    System.out.println("Unsupported field type: " + key + " with value: " + value);
                }
            }
        } else {
            // 获取所有字段（包括父类字段）
            List<Field> allFields = getAllFields(targetObject.getClass());

            for (Field field : allFields) {
                field.setAccessible(true); // 确保字段可访问
                try {
                    // 尝试通过 get 方法获取字段值
                    Object value = getFieldValue(targetObject, field);
                    if (value != null) { // 避免存储 null 值
                        // 检查字段类型并处理
                        if (isLeanCloudSupportedType(value)) {
                            lcObject.put(field.getName(), value);
                        } else {
                            lcObject.put(field.getName(), value.toString());
                            System.out.println("[cast to String]Unsupported field type: " + field.getName() + " with value: " + value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("Failed to access field: " + field.getName());
                    return false;
                }
            }
        }

        // 设置 ACL
//        LCACL acl = new LCACL();
//        acl.setReadAccess(userId, true);
//        acl.setWriteAccess(userId, true);
//        lcObject.setACL(acl);

        Observable<? extends LCObject> observable = lcObject.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Object creation failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean isLeanCloudSupportedType(Object value) {
        return (value instanceof String)
            || (value instanceof Number)
            || (value instanceof Boolean)
            || (value instanceof Date)
//            || (value instanceof LCObject)
//            || (value instanceof List)
//            || (value instanceof Set)
    ;}
    // 获取代理对象的目标对象
    private static Object getTargetObject(Object object) {
        if (object == null) {
            return null;
        }
        // 如果 object 是 java.lang.reflect.Proxy 生成的代理对象
        if (Proxy.isProxyClass(object.getClass())) {
            try {
                Field h = object.getClass().getDeclaredField("h");
                h.setAccessible(true);
                InvocationHandler handler = (InvocationHandler) h.get(object);

                Field target = handler.getClass().getDeclaredField("target");
                target.setAccessible(true);
                return target.get(handler);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 如果 object 是 CGLIB 生成的代理对象
        if (object.getClass().getName().contains("$$")) {
            try {
                Field callbackField = object.getClass().getDeclaredField("CGLIB$CALLBACK_0");
                callbackField.setAccessible(true);
                Object callback = callbackField.get(object);

                Field targetField = callback.getClass().getDeclaredField("CGLIB$CALLBACK_0");
                targetField.setAccessible(true);
                return targetField.get(callback);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 如果 object 不是代理对象，直接返回
        return object;
    }
    // 递归获取所有字段（包括父类字段）
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    // 尝试通过 get 方法获取字段值
    private static Object getFieldValue(Object object, Field field) throws IllegalAccessException {
        try {
            String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method getterMethod = object.getClass().getMethod(getterName);
            return getterMethod.invoke(object);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            // 如果没有 get 方法，直接访问字段
            return field.get(object);
        }
    }

    // 泛型方法：查询对象
    public static <T> List<T> queryObject(String className, String key, Object value, String userId, Class<T> clazz) {
        LCQuery<LCObject> query = new LCQuery<>(className);
        query.whereEqualTo(key, value);

        Observable<List<LCObject>> observable = query.findInBackground();
        try {
            List<LCObject> results = observable.blockingFirst();
            return results.stream()
//                .filter(obj -> {
//                    LCACL acl = obj.getACL();
//                    return acl != null && acl.getReadAccess(userId);
//                })
                    .map(obj -> convertToGenericObject(obj, clazz))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static <T> T convertToGenericObject(LCObject lcObject, Class<T> clazz) {
        try {
            if (clazz == Map.class) {
                Map<String, Object> map = new HashMap<>();
                for (String key : lcObject.getServerData().keySet()) {
                    map.put(key, lcObject.get(key));
                }
                return clazz.cast(map);
            } else {
                T genericObject = clazz.getDeclaredConstructor().newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(genericObject, lcObject.get(field.getName()));
                }
                return genericObject;
            }
        } catch (Exception e) {
            System.out.println("Failed to convert LCObject to generic object: " + e.getMessage());
            return null;
        }
    }
    // 泛型方法：更新对象
    public static <T> boolean updateObject(String className, String objectId, T object, String userId) {
        LCObject lcObject = LCObject.createWithoutData(className, objectId);

        // 使用反射解析对象字段并设置到 LCObject
        List<Field> allFields = getAllFields(object.getClass());
        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value != null) { // 避免存储 null 值
                    if (isLeanCloudSupportedType(value)) {
                        lcObject.put(field.getName(), value);
                    } else {
                        lcObject.put(field.getName(), value.toString());
                        System.out.println("[cast to String]Unsupported field type: " + field.getName() + " with value: " + value);
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Failed to access field: " + field.getName());
                return false;
            }
        }

        // 检查权限（可选）
//    LCACL acl = lcObject.getACL();
//    if (acl != null && acl.getWriteAccess(userId)) {
        Observable<? extends LCObject> observable = lcObject.saveInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Object update failed: " + e.getMessage());
            return false;
        }
//    } else {
//        System.out.println("No permission to update this object.");
//        return false;
//    }
    }

    // 泛型方法：删除对象
    public static boolean deleteObject(String className, String objectId, String userId) {
        LCObject lcObject = LCObject.createWithoutData(className, objectId);

        // 检查权限（可选）
//    LCACL acl = lcObject.getACL();
//    if (acl != null && acl.getWriteAccess(userId)) {
        Observable<LCNull> observable = lcObject.deleteInBackground();
        try {
            observable.blockingFirst();
            return true;
        } catch (Exception e) {
            System.out.println("Object deletion failed: " + e.getMessage());
            return false;
        }
//    } else {
//        System.out.println("No permission to delete this object.");
//        return false;
//    }
    }
}