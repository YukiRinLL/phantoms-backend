package com.phantoms.phantomsbackend.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DaoYuKeyCacheService {

    private static final Logger logger = LoggerFactory.getLogger(DaoYuKeyCacheService.class);

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取DaoYu Key，带有缓存功能
     * 除非结果为null，否则都会缓存
     */
    @Cacheable(value = "daoyuKey", unless = "#result == null")
    public String getDaoYuKey() {
        logger.info("从数据库加载DaoYu Key...");
        String key = systemConfigService.getDaoYuKey();
        logger.info("DaoYu Key加载完成: {}", key);
        return key;
    }

    /**
     * 手动清除缓存
     */
    @CacheEvict(value = "daoyuKey", allEntries = true)
    public void evictDaoYuKeyCache() {
        logger.info("手动清除DaoYu Key缓存");
    }

    /**
     * 定时清除缓存（每5分钟执行一次）
     * 下次访问时会自动重新加载
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    @CacheEvict(value = "daoyuKey", allEntries = true)
    public void scheduledEvictCache() {
        logger.info("定时清除DaoYu Key缓存");
    }

    /**
     * 应用启动时预加载缓存
     */
    @PostConstruct
    public void preloadCache() {
        logger.info("应用启动，预加载DaoYu Key缓存");
        getDaoYuKey(); // 触发缓存加载
    }
}