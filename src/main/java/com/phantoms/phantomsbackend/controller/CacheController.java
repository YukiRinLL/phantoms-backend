package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.DaoYuKeyCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private DaoYuKeyCacheService daoYuKeyCacheService;

    /**
     * 手动刷新DaoYu Key缓存
     */
    @PostMapping("/refresh-daoyu-key")
    public JSONObject refreshDaoYuKeyCache() {
        JSONObject result = new JSONObject();
        try {
            daoYuKeyCacheService.evictDaoYuKeyCache();
            // 触发重新加载
            daoYuKeyCacheService.getDaoYuKey();
            result.put("success", true);
            result.put("message", "DaoYu Key缓存刷新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "缓存刷新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取缓存状态
     */
    @PostMapping("/cache-status")
    public JSONObject getCacheStatus() {
        JSONObject result = new JSONObject();
        try {
            String currentKey = daoYuKeyCacheService.getDaoYuKey();
            result.put("success", true);
            result.put("cached", true);
            result.put("key", currentKey.substring(0, Math.min(10, currentKey.length())) + "***"); // 部分显示
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取缓存状态失败: " + e.getMessage());
        }
        return result;
    }
}