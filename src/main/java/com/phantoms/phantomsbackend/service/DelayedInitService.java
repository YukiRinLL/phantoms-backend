package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.service.scheduler.FF14CrystalNewsScheduler;
import com.phantoms.phantomsbackend.service.scheduler.FF14GlobalNewsScheduler;
import com.phantoms.phantomsbackend.service.scheduler.FF14NewsScheduler;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DelayedInitService {

    private static final Logger logger = LoggerFactory.getLogger(DelayedInitService.class);

    @Autowired(required = false)
    private FF14NewsScheduler ff14NewsScheduler;

    @Autowired(required = false)
    private FF14CrystalNewsScheduler ff14CrystalNewsScheduler;

    @Autowired(required = false)
    private FF14GlobalNewsScheduler ff14GlobalNewsScheduler;

    @Autowired(required = false)
    private DaoYuKeyCacheService daoYuKeyCacheService;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void onApplicationReady() {
        logger.info("应用启动完成，开始延迟初始化缓存...");
        
        // 延迟1秒开始初始化，避免启动时内存峰值
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 逐个初始化缓存，避免同时加载
        initDaoYuKeyCache();
        initFF14NewsCache();
        initFF14CrystalNewsCache();
        initFF14GlobalNewsCache();

        logger.info("延迟初始化缓存完成");
    }

    private void initDaoYuKeyCache() {
        if (daoYuKeyCacheService != null) {
            try {
                logger.info("开始初始化DaoYu Key缓存");
                daoYuKeyCacheService.preloadCache();
                logger.info("DaoYu Key缓存初始化完成");
            } catch (Exception e) {
                logger.error("初始化DaoYu Key缓存失败", e);
            }
        }
    }

    private void initFF14NewsCache() {
        if (ff14NewsScheduler != null) {
            try {
                logger.info("开始初始化FF14国服新闻缓存");
                ff14NewsScheduler.initCache();
                logger.info("FF14国服新闻缓存初始化完成");
            } catch (Exception e) {
                logger.error("初始化FF14国服新闻缓存失败", e);
            }
        }
    }

    private void initFF14CrystalNewsCache() {
        if (ff14CrystalNewsScheduler != null) {
            try {
                logger.info("开始初始化FF14水晶世界新闻缓存");
                ff14CrystalNewsScheduler.initCache();
                logger.info("FF14水晶世界新闻缓存初始化完成");
            } catch (Exception e) {
                logger.error("初始化FF14水晶世界新闻缓存失败", e);
            }
        }
    }

    private void initFF14GlobalNewsCache() {
        if (ff14GlobalNewsScheduler != null) {
            try {
                logger.info("开始初始化FF14国际服新闻缓存");
                ff14GlobalNewsScheduler.initCache();
                logger.info("FF14国际服新闻缓存初始化完成");
            } catch (Exception e) {
                logger.error("初始化FF14国际服新闻缓存失败", e);
            }
        }
    }
}
