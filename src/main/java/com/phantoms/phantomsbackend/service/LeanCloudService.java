package com.phantoms.phantomsbackend.service;

import cn.leancloud.LCObject;
import java.util.List;

public interface LeanCloudService {
    boolean storeKV(String key, String value, String userId);
    String getKV(String key, String userId);
    boolean updateKV(String key, String newValue, String userId);
    boolean deleteKV(String key, String userId);
    <T> boolean storeObject(String className, T object, String userId);
    <T> T getObject(String className, String objectId, Class<T> clazz, String userId);
    <T> boolean updateObject(String className, String objectId, T object, String userId);
    boolean deleteObject(String className, String objectId, String userId);
    <T> List<T> queryObject(String className, String key, Object value, Class<T> clazz, String userId);
}