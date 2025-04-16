package com.phantoms.phantomsbackend.service;

import cn.leancloud.LCObject;
import java.util.List;

public interface LeanCloudService {
    boolean storeKV(String key, String value);
    String getKV(String key);
    boolean updateKV(String key, String newValue);
    boolean deleteKV(String key);
    boolean createObject(String className, String key, String value);
    List<LCObject> queryObject(String className, String key, String value);
    boolean updateObject(String className, String objectId, String key, String newValue);
    boolean deleteObject(String className, String objectId);
}