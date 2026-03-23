package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.FF14CrystalNewsUtils;
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
public class FF14CrystalNewsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FF14CrystalNewsScheduler.class);

    private static final String FF14_CRYSTAL_NEWS_CACHE_KEY = "news:crystal:last_ids";
    
    private List<String> inMemoryCache = new ArrayList<>();

    @Autowired
    private FF14CrystalNewsUtils ff14CrystalNewsUtils;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${napcat.phantom-group-id}")
    private String phantomGroupId;

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    @Value("${napcat.crystal-group-id}")
    private String crystalGroupId;

    @PostConstruct
    public void initCache() {
        logger.info("初始化FF14水晶世界新闻缓存");
        try {
            try {
                Object cachedIdsObj = redisUtil.get(FF14_CRYSTAL_NEWS_CACHE_KEY);
                if (cachedIdsObj instanceof List) {
                    inMemoryCache = (List<String>) cachedIdsObj;
                    logger.info("从Redis加载缓存成功，共 {} 条水晶世界新闻ID", inMemoryCache.size());
                    return;
                }
            } catch (Exception e) {
                logger.warn("从Redis读取缓存失败: {}", e.getMessage());
            }

            logger.info("Redis不可用，从新闻源获取初始缓存");
            List<FF14CrystalNewsUtils.NewsItem> newsList = ff14CrystalNewsUtils.fetchCrystalNews();
            if (!newsList.isEmpty()) {
                inMemoryCache = newsList.stream()
                    .map(FF14CrystalNewsUtils.NewsItem::getId)
                    .collect(Collectors.toList());
                logger.info("从新闻源加载初始缓存成功，共 {} 条水晶世界新闻ID", inMemoryCache.size());
            } else {
                logger.warn("从新闻源获取初始缓存失败，使用空缓存");
            }
        } catch (Exception e) {
            logger.error("初始化缓存失败", e);
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void fetchAndSendFF14CrystalNews() {
        long start = System.currentTimeMillis();
        logger.info("开始获取FF14水晶世界新闻列表（耗时监控）");

        try {
            // 任务超时控制：超过30秒则中断
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    List<FF14CrystalNewsUtils.NewsItem> newsList = ff14CrystalNewsUtils.fetchCrystalNews();

                    if (newsList.isEmpty()) {
                        logger.warn("未获取到FF14水晶世界新闻");
                        return;
                    }

                    List<String> currentIds = newsList.stream()
                        .map(FF14CrystalNewsUtils.NewsItem::getId)
                        .collect(Collectors.toList());

                    List<String> cachedIds = new ArrayList<>();
                    try {
                        Object cachedIdsObj = redisUtil.get(FF14_CRYSTAL_NEWS_CACHE_KEY);
                        if (cachedIdsObj instanceof List) {
                            cachedIds = (List<String>) cachedIdsObj;
                        }
                    } catch (Exception e) {
                        logger.warn("Redis读取缓存失败，使用内存缓存: {}", e.getMessage());
                        cachedIds = new ArrayList<>(inMemoryCache);
                    }

                    List<String> finalCachedIds = cachedIds;
                    List<FF14CrystalNewsUtils.NewsItem> newNewsList = newsList.stream()
                        .filter(news -> !finalCachedIds.contains(news.getId()))
                        .collect(Collectors.toList());

                    if (newNewsList.size() > 5) {
                        logger.warn("检测到缓存丢失，新新闻数量 {} 条超过阈值，使用最新新闻更新缓存", newNewsList.size());
                        try {
                            redisUtil.set(FF14_CRYSTAL_NEWS_CACHE_KEY, currentIds);
                            inMemoryCache = new ArrayList<>(currentIds);
                            logger.info("缓存已更新，共 {} 条水晶世界新闻ID", currentIds.size());
                        } catch (Exception e) {
                            logger.warn("Redis更新缓存失败，仅更新内存缓存: {}", e.getMessage());
                            inMemoryCache = new ArrayList<>(currentIds);
                        }
                        return;
                    }

                    if (!newNewsList.isEmpty()) {
                        logger.info("发现 {} 条FF14水晶世界新新闻", newNewsList.size());
                        sendNewsToGroup(newNewsList);
                    } else {
                        logger.debug("没有FF14水晶世界新新闻");
                    }

                    try {
                        redisUtil.set(FF14_CRYSTAL_NEWS_CACHE_KEY, currentIds);
                        inMemoryCache = new ArrayList<>(currentIds);
                    } catch (Exception e) {
                        logger.warn("Redis更新缓存失败，仅更新内存缓存: {}", e.getMessage());
                        inMemoryCache = new ArrayList<>(currentIds);
                    }

                } catch (Exception e) {
                    logger.error("获取FF14水晶世界新闻失败", e);
                }
            }).orTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .exceptionally(e -> {
                logger.error("定时任务执行超时/异常", e);
                return null;
            }).join();

        } catch (Throwable e) {
            logger.error("定时任务主流程异常", e);
        } finally {
            logger.info("获取FF14水晶世界新闻列表结束，耗时 {}ms", System.currentTimeMillis() - start);
        }
    }

    private void sendNewsToGroup(List<FF14CrystalNewsUtils.NewsItem> newsList) {
        try {
            for (FF14CrystalNewsUtils.NewsItem news : newsList) {
                StringBuilder message = new StringBuilder();
//                message.append("【FF14水晶世界新闻】\n");
                
                if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                    message.append("[CQ:image,file=").append(news.getImageUrl()).append("]");
                }
                message.append(news.getTitle()).append("\n");
                if (news.getAuthor() != null && !news.getAuthor().isEmpty()) {
                    message.append("创建者: ").append(news.getAuthor()).append("\n");
                }
                if (news.getDate() != null && !news.getDate().isEmpty()) {
                    message.append("创建时间: ").append(news.getDate()).append("\n");
                }
                if (news.getLinkUrl() != null && !news.getLinkUrl().isEmpty()) {
                    message.append("详情链接: ").append(news.getLinkUrl()).append("\n");
                }

                napCatQQUtil.sendGroupMessage(defaultGroupId, message.toString());
                logger.info("已发送FF14水晶世界新闻: {}", news.getTitle());
                
                Thread.sleep(1000);
            }
            
            logger.info("成功发送 {} 条FF14水晶世界新闻到QQ群", newsList.size());

        } catch (Exception e) {
            logger.error("发送FF14水晶世界新闻到QQ群失败", e);
        }
    }
}
