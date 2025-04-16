package com.phantoms.phantomsbackend.service;

import cn.leancloud.LCObject;
import java.util.List;

public interface LeanCloudService {
    boolean storeKV(String key, String value, String userId);
    String getKV(String key, String userId);
    boolean updateKV(String key, String newValue, String userId);
    boolean deleteKV(String key, String userId);
    boolean createObject(String className, String key, String value, String userId);
    List<LCObject> queryObject(String className, String key, String value, String userId);
    boolean updateObject(String className, String objectId, String key, String newValue, String userId);
    boolean deleteObject(String className, String objectId, String userId);
}