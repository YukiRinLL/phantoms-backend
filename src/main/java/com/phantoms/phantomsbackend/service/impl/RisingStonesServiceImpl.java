package com.phantoms.phantomsbackend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.RedisUtil;
//import com.phantoms.phantomsbackend.common.utils.RisingStonesLoginTool;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class RisingStonesServiceImpl implements RisingStonesService {

    private static final Logger logger = LoggerFactory.getLogger(RisingStonesServiceImpl.class);

//    @Autowired
//    private RisingStonesLoginTool risingStonesLoginTool;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    // Redis缓存键前缀
    private static final String USER_INFO_CACHE_PREFIX = "user:info:";
    private static final String GUILD_INFO_CACHE_PREFIX = "guild:info:";
    private static final String GUILD_MEMBER_CACHE_PREFIX = "guild:member:";
    private static final String GUILD_MEMBER_DYNAMIC_CACHE_PREFIX = "guild:member:dynamic:";

    // 异步写入缓存的方法
    @Async
    private void asyncCacheResult(String key, JSONObject result) {
        // 设置为永不过期的缓存
        redisUtil.set(key, result);
    }

//    private synchronized void ensureTokenAndCookie() throws IOException {
//        long currentTime = System.currentTimeMillis();
//
//        // 从数据库获取当前的token、cookie和获取时间
//        String currentDaoyuToken = systemConfigService.getDaoyuToken();
//        String currentCookie = systemConfigService.getLoginCookies();
//        long tokenObtainTime = systemConfigService.getTokenObtainTime();
//
//        if (currentDaoyuToken == null
//            || currentCookie == null
//            || currentTime - tokenObtainTime > TimeUnit.MINUTES.toMillis(720) // 判断DaoyuToken在12小时后过期
//        ) {
//            // 获取新的token和cookie
//            String[] tokenAndCookie = risingStonesLoginTool.getDaoYuTokenAndCookie();
//            String newDaoyuToken = tokenAndCookie[0];
//            String newCookie = tokenAndCookie[1];
//
//            // 保存到数据库
//            systemConfigService.updateDaoyuToken(newDaoyuToken);
//            systemConfigService.updateLoginCookies(newCookie);
//            systemConfigService.updateTokenObtainTime(currentTime);
//        }
//    }

    @Override
    public JSONObject getUserInfo(String uuid) throws IOException {
        String cacheKey = USER_INFO_CACHE_PREFIX + uuid;
        JSONObject result = null;
        
        try {
            // 先尝试从叨鱼工具查询
//            ensureTokenAndCookie();
            result = RisingStonesUtils.getUserInfo(uuid);
            
            // 如果查询成功，异步写入缓存
            if (result != null && result.getInteger("code") == 10000) {
                asyncCacheResult(cacheKey, result);
            }
        } catch (Exception e) {
            // 查询失败，从缓存获取
            logger.error("Failed to get user info from RisingStonesUtils, trying cache", e);
            result = (JSONObject) redisUtil.get(cacheKey);
            
            // 如果缓存也没有，返回空或错误
            if (result == null) {
                result = new JSONObject();
                result.put("code", 500);
                result.put("message", "Failed to get user info");
            }
        }
        
        return result;
    }

    @Override
    public JSONObject getGuildInfo(String guildId) throws IOException {
        String cacheKey = GUILD_INFO_CACHE_PREFIX + guildId;
        JSONObject result = null;
        
        try {
            // 先尝试从叨鱼工具查询
//            ensureTokenAndCookie();
            result = RisingStonesUtils.getGuildInfo(guildId);
            
            // 如果查询成功，异步写入缓存
            if (result != null && result.getInteger("code") == 10000) {
                asyncCacheResult(cacheKey, result);
            }
        } catch (Exception e) {
            // 查询失败，从缓存获取
            logger.error("Failed to get guild info from RisingStonesUtils, trying cache", e);
            result = (JSONObject) redisUtil.get(cacheKey);
            
            // 如果缓存也没有，返回空或错误
            if (result == null) {
                result = new JSONObject();
                result.put("code", 500);
                result.put("message", "Failed to get guild info");
            }
        }
        
        return result;
    }

    @Override
    public JSONObject getGuildMember(String guildId) throws IOException {
        String cacheKey = GUILD_MEMBER_CACHE_PREFIX + guildId;
        JSONObject result = null;
        
        try {
            // 先尝试从叨鱼工具查询
//            ensureTokenAndCookie();
            result = RisingStonesUtils.getGuildMember(guildId);
            
            // 如果查询成功，异步写入缓存
            if (result != null && result.getInteger("code") == 10000) {
                asyncCacheResult(cacheKey, result);
            }
        } catch (Exception e) {
            // 查询失败，从缓存获取
            logger.error("Failed to get guild member from RisingStonesUtils, trying cache", e);
            result = (JSONObject) redisUtil.get(cacheKey);
            
            // 如果缓存也没有，返回空或错误
            if (result == null) {
                result = new JSONObject();
                result.put("code", 500);
                result.put("message", "Failed to get guild member info");
            }
        }
        
        return result;
    }

    @Override
    public JSONObject getGuildMemberDynamic(String guildId, int page, int limit) throws IOException {
        String cacheKey = GUILD_MEMBER_DYNAMIC_CACHE_PREFIX + guildId + ":" + page + ":" + limit;
        JSONObject result = null;
        
        try {
            // 先尝试从叨鱼工具查询
//            ensureTokenAndCookie();
            result = RisingStonesUtils.getGuildMemberDynamic(guildId, page, limit);
            
            // 如果查询成功，异步写入缓存
            if (result != null && result.getInteger("code") == 10000) {
                asyncCacheResult(cacheKey, result);
            }
        } catch (Exception e) {
            // 查询失败，从缓存获取
            logger.error("Failed to get guild member dynamic from RisingStonesUtils, trying cache", e);
            result = (JSONObject) redisUtil.get(cacheKey);
            
            // 如果缓存也没有，返回空或错误
            if (result == null) {
                result = new JSONObject();
                result.put("code", 500);
                result.put("message", "Failed to get guild member dynamic info");
            }
        }
        
        return result;
    }
}