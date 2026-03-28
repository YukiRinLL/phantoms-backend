package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.DaoYuKeyCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
@Tag(name = "Cache", description = "缓存管理接口")
public class CacheController {

    @Autowired
    private DaoYuKeyCacheService daoYuKeyCacheService;

    @PostMapping("/refresh-daoyu-key")
    @Operation(
            summary = "刷新DaoYu Key缓存",
            description = "手动刷新DaoYu Key缓存",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "缓存刷新成功或失败")
            }
    )
    public JSONObject refreshDaoYuKeyCache() {
        JSONObject result = new JSONObject();
        try {
            daoYuKeyCacheService.evictDaoYuKeyCache();
            daoYuKeyCacheService.getDaoYuKey();
            result.put("success", true);
            result.put("message", "DaoYu Key缓存刷新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "缓存刷新失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/cache-status")
    @Operation(
            summary = "获取缓存状态",
            description = "获取当前DaoYu Key缓存的状态信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取缓存状态成功或失败")
            }
    )
    public JSONObject getCacheStatus() {
        JSONObject result = new JSONObject();
        try {
            String currentKey = daoYuKeyCacheService.getDaoYuKey();
            result.put("success", true);
            result.put("cached", true);
            result.put("key", currentKey.substring(0, Math.min(10, currentKey.length())) + "***");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取缓存状态失败: " + e.getMessage());
        }
        return result;
    }
}
