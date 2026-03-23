package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.FF14GlobalNewsUtils;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FF14GlobalNewsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FF14GlobalNewsScheduler.class);

    private static final String FF14_GLOBAL_NEWS_CACHE_KEY = "news:lodestone:last_ids";
    
    // 内存缓存，当Redis不可用时使用
    private List<String> inMemoryCache = new ArrayList<>();

    @Autowired
    private FF14GlobalNewsUtils ff14GlobalNewsUtils;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${napcat.phantom-group-id}")
    private String phantomGroupId;

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    @PostConstruct
    public void initCache() {
        logger.info("初始化FF14国际服新闻缓存");
        try {
            // 尝试从Redis读取缓存
            try {
                Object cachedIdsObj = redisUtil.get(FF14_GLOBAL_NEWS_CACHE_KEY);
                if (cachedIdsObj instanceof List) {
                    inMemoryCache = (List<String>) cachedIdsObj;
                    logger.info("从Redis加载缓存成功，共 {} 条国际服新闻ID", inMemoryCache.size());
                    return;
                }
            } catch (Exception e) {
                logger.warn("从Redis读取缓存失败: {}", e.getMessage());
            }

            // 如果Redis不可用，从新闻源获取最新新闻列表作为初始缓存
            logger.info("Redis不可用，从新闻源获取初始缓存");
            List<FF14GlobalNewsUtils.NewsItem> newsList = ff14GlobalNewsUtils.fetchGlobalNews();
            if (!newsList.isEmpty()) {
                inMemoryCache = newsList.stream()
                    .map(FF14GlobalNewsUtils.NewsItem::getId)
                    .collect(Collectors.toList());
                logger.info("从新闻源加载初始缓存成功，共 {} 条国际服新闻ID", inMemoryCache.size());
            } else {
                logger.warn("从新闻源获取初始缓存失败，使用空缓存");
            }
        } catch (Exception e) {
            logger.error("初始化缓存失败", e);
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void fetchAndSendFF14GlobalNews() {
        long start = System.currentTimeMillis();
        logger.info("开始获取FF14国际服新闻列表（耗时监控）");

        try {
            // 任务超时控制：超过30秒则中断
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    List<FF14GlobalNewsUtils.NewsItem> newsList = ff14GlobalNewsUtils.fetchGlobalNews();

                    if (newsList.isEmpty()) {
                        logger.warn("未获取到FF14国际服新闻");
                        return;
                    }

                    List<String> currentIds = newsList.stream()
                        .map(FF14GlobalNewsUtils.NewsItem::getId)
                        .collect(Collectors.toList());

                    // 获取缓存的新闻ID，增加异常处理
                    List<String> cachedIds = new ArrayList<>();
                    try {
                        Object cachedIdsObj = redisUtil.get(FF14_GLOBAL_NEWS_CACHE_KEY);
                        if (cachedIdsObj instanceof List) {
                            cachedIds = (List<String>) cachedIdsObj;
                        }
                    } catch (Exception e) {
                        logger.warn("Redis读取缓存失败，使用内存缓存: {}", e.getMessage());
                        cachedIds = new ArrayList<>(inMemoryCache);
                    }

                    List<String> finalCachedIds = cachedIds;
                    List<FF14GlobalNewsUtils.NewsItem> newNewsList = newsList.stream()
                        .filter(news -> !finalCachedIds.contains(news.getId()))
                        .collect(Collectors.toList());

                    // 如果新新闻数量超过5条，判定为缓存丢失
                    if (newNewsList.size() > 5) {
                        logger.warn("检测到缓存丢失，新新闻数量 {} 条超过阈值，使用最新新闻更新缓存", newNewsList.size());
                        // 更新缓存为当前所有新闻ID，不发送消息
                        try {
                            redisUtil.set(FF14_GLOBAL_NEWS_CACHE_KEY, currentIds);
                            inMemoryCache = new ArrayList<>(currentIds);
                            logger.info("缓存已更新，共 {} 条国际服新闻ID", currentIds.size());
                        } catch (Exception e) {
                            logger.warn("Redis更新缓存失败，仅更新内存缓存: {}", e.getMessage());
                            inMemoryCache = new ArrayList<>(currentIds);
                        }
                        return;
                    }

                    if (!newNewsList.isEmpty()) {
                        logger.info("发现 {} 条FF14国际服新新闻", newNewsList.size());
                        sendNewsToGroup(newNewsList);
                    } else {
                        logger.debug("没有FF14国际服新新闻");
                    }

                    // 更新缓存，增加异常处理
                    try {
                        redisUtil.set(FF14_GLOBAL_NEWS_CACHE_KEY, currentIds);
                        // 同时更新内存缓存
                        inMemoryCache = new ArrayList<>(currentIds);
                    } catch (Exception e) {
                        logger.warn("Redis更新缓存失败，仅更新内存缓存: {}", e.getMessage());
                        inMemoryCache = new ArrayList<>(currentIds);
                    }

                } catch (Exception e) {
                    logger.error("获取FF14国际服新闻失败", e);
                }
            }).orTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .exceptionally(e -> {
                logger.error("定时任务执行超时/异常", e);
                return null;
            }).join();

        } catch (Throwable e) {
            logger.error("定时任务主流程异常", e);
        } finally {
            logger.info("获取FF14国际服新闻列表结束，耗时 {}ms", System.currentTimeMillis() - start);
        }
    }

    private void sendNewsToGroup(List<FF14GlobalNewsUtils.NewsItem> newsList) {
        try {
            for (FF14GlobalNewsUtils.NewsItem news : newsList) {
                StringBuilder message = new StringBuilder();
                message.append("【FF14国际服新闻】\n");
                
                if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                    message.append("[CQ:image,file=").append(news.getImageUrl()).append("]");
                }
                message.append(news.getTitle()).append("\n");
                if (news.getDescription() != null && !news.getDescription().isEmpty() && !news.getDescription().equals(news.getTitle())) {
                    message.append(news.getDescription()).append("\n");
                }
                message.append(news.getDate()).append("\n");
                if (news.getLinkUrl() != null && !news.getLinkUrl().isEmpty()) {
                    message.append(news.getLinkUrl());
                }

                napCatQQUtil.sendGroupMessage(phantomGroupId, message.toString());
                logger.info("已发送FF14国际服新闻: {}", news.getTitle());
                
                Thread.sleep(1000);
            }
            
            logger.info("成功发送 {} 条FF14国际服新闻到QQ群", newsList.size());

        } catch (Exception e) {
            logger.error("发送FF14国际服新闻到QQ群失败", e);
        }
    }
}
