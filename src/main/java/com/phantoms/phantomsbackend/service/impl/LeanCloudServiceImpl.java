package com.phantoms.phantomsbackend.service.impl;

import java.util.List;

import com.phantoms.phantomsbackend.common.utils.LeanCloudUtils;
import com.phantoms.phantomsbackend.service.LeanCloudService;

import org.springframework.stereotype.Service;

@Service
public class LeanCloudServiceImpl implements LeanCloudService {

    @Override
    public boolean storeKV(String key, String value, String userId) {
        return LeanCloudUtils.storeKV(key, value, userId);
    }

    @Override
    public String getKV(String key, String userId) {
        return LeanCloudUtils.getKV(key, userId);
    }

    @Override
    public boolean updateKV(String key, String newValue, String userId) {
        return LeanCloudUtils.updateKV(key, newValue, userId);
    }

    @Override
    public boolean deleteKV(String key, String userId) {
        return LeanCloudUtils.deleteKV(key, userId);
    }

    @Override
    public <T> boolean storeObject(String className, T object, String userId) {
        return LeanCloudUtils.createObject(className, object, userId);
    }

    @Override
    public <T> T getObject(String className, String objectId, Class<T> clazz, String userId) {
        return LeanCloudUtils.queryObject(className, "objectId", objectId, userId, clazz).stream().findFirst().orElse(null);
    }

    @Override
    public <T> boolean updateObject(String className, String objectId, T object, String userId) {
        return LeanCloudUtils.updateObject(className, objectId, object, userId);
    }

    @Override
    public boolean deleteObject(String className, String objectId, String userId) {
        return LeanCloudUtils.deleteObject(className, objectId, userId);
    }

    @Override
    public <T> List<T> queryObject(String className, String key, Object value, Class<T> clazz, String userId) {
        return LeanCloudUtils.queryObject(className, key, value, userId, clazz);
    }
}