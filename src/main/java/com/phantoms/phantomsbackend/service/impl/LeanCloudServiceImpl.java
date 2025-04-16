package com.phantoms.phantomsbackend.service.impl;

import cn.leancloud.LCObject;
import com.phantoms.phantomsbackend.common.utils.LeanCloudUtils;
import com.phantoms.phantomsbackend.service.LeanCloudService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public boolean createObject(String className, String key, String value, String userId) {
        return LeanCloudUtils.createObject(className, key, value, userId);
    }

    @Override
    public List<LCObject> queryObject(String className, String key, String value, String userId) {
        return LeanCloudUtils.queryObject(className, key, value, userId);
    }

    @Override
    public boolean updateObject(String className, String objectId, String key, String newValue, String userId) {
        return LeanCloudUtils.updateObject(className, objectId, key, newValue, userId);
    }

    @Override
    public boolean deleteObject(String className, String objectId, String userId) {
        return LeanCloudUtils.deleteObject(className, objectId, userId);
    }
}