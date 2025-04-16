package com.phantoms.phantomsbackend.service.impl;

import cn.leancloud.LCObject;
import com.phantoms.phantomsbackend.common.utils.LeanCloudUtils;
import com.phantoms.phantomsbackend.service.LeanCloudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeanCloudServiceImpl implements LeanCloudService {

    @Override
    public boolean storeKV(String key, String value) {
        return LeanCloudUtils.storeKV(key, value);
    }

    @Override
    public String getKV(String key) {
        return LeanCloudUtils.getKV(key);
    }

    @Override
    public boolean updateKV(String key, String newValue) {
        return LeanCloudUtils.updateKV(key, newValue);
    }

    @Override
    public boolean deleteKV(String key) {
        return LeanCloudUtils.deleteKV(key);
    }

    @Override
    public boolean createObject(String className, String key, String value) {
        return LeanCloudUtils.createObject(className, key, value);
    }

    @Override
    public List<LCObject> queryObject(String className, String key, String value) {
        return LeanCloudUtils.queryObject(className, key, value);
    }

    @Override
    public boolean updateObject(String className, String objectId, String key, String newValue) {
        return LeanCloudUtils.updateObject(className, objectId, key, newValue);
    }

    @Override
    public boolean deleteObject(String className, String objectId) {
        return LeanCloudUtils.deleteObject(className, objectId);
    }
}