package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.CyouClient;
import com.phantoms.phantomsbackend.common.utils.RedisUtil;
import com.phantoms.phantomsbackend.pojo.entity.HousingSale;
import com.phantoms.phantomsbackend.service.OneBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class HousingSaleScheduler {

    private static final Logger logger = LoggerFactory.getLogger(HousingSaleScheduler.class);

    private static final String HOUSING_SALE_CACHE_PREFIX = "housing_sale:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Autowired
    private OneBotService oneBotService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${housing.sale.notify.servers:1121,1081}")
    private String notifyServers;

    @Value("${housing.sale.notify.areas:0,1,2,3,4}")
    private String notifyAreas;

    // 每天上午10点，下午2点，晚上8点执行
    @Scheduled(cron = "0 0 10,14,20 * * ?")
    public void fetchAndProcessHousingSales() {
        try {
            logger.info("开始获取房屋销售数据...");

            List<String> servers = Arrays.asList(notifyServers.split(","));
            List<HousingSale> allHousingSales = new ArrayList<>();

            for (String server : servers) {
                if ("default".equals(server.trim())) {
                    continue;
                }
                try {
                    List<HousingSale> serverSales = CyouClient.fetchHousingSales(server.trim());
                    allHousingSales.addAll(serverSales);
                    logger.info("服务器 {} 获取到 {} 条房屋销售数据", server, serverSales.size());

                    Thread.sleep(1000);
                } catch (Exception e) {
                    logger.error("获取服务器 {} 的房屋销售数据失败", server, e);
                }
            }

            if (!allHousingSales.isEmpty()) {
                int savedCount = robustSaveHousingSales(allHousingSales);
                logger.info("成功保存 {} 条房屋销售数据", savedCount);

                filterAndNotifyNewHouses(allHousingSales);
            } else {
                logger.info("未获取到任何房屋销售数据");
            }

        } catch (Exception e) {
            logger.error("获取和处理房屋销售数据失败", e);
        }
    }

    /**
     * 稳健的房屋数据保存方法
     */
    private int robustSaveHousingSales(List<HousingSale> housingSales) {
        if (housingSales.isEmpty()) {
            return 0;
        }

        logger.info("开始稳健保存 {} 条房屋数据", housingSales.size());

        int successCount = 0;
        int errorCount = 0;

        String sql = "INSERT INTO public.housing_sales " +
            "(server_id, area, slot, house_id, price, size, first_seen, last_seen, " +
            "purchase_type, region_type, state, participate, winner, end_time, update_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (server_id, area, slot, house_id) DO UPDATE SET " +
            "price = EXCLUDED.price, " +
            "last_seen = EXCLUDED.last_seen, " +
            "purchase_type = EXCLUDED.purchase_type, " +
            "state = EXCLUDED.state, " +
            "participate = EXCLUDED.participate, " +
            "winner = EXCLUDED.winner, " +
            "end_time = EXCLUDED.end_time, " +
            "update_time = EXCLUDED.update_time";

        for (int i = 0; i < housingSales.size(); i++) {
            HousingSale sale = housingSales.get(i);

            try {
                Integer result = transactionTemplate.execute(status -> {
                    try {
                        return jdbcTemplate.update(sql,
                            sale.getServer(),
                            sale.getArea(),
                            sale.getSlot(),
                            sale.getId(),
                            sale.getPrice(),
                            sale.getSize(),
                            sale.getFirstSeen() != null ? Timestamp.valueOf(sale.getFirstSeen().toLocalDateTime()) : null,
                            sale.getLastSeen() != null ? Timestamp.valueOf(sale.getLastSeen().toLocalDateTime()) : null,
                            sale.getPurchaseType(),
                            sale.getRegionType(),
                            sale.getState(),
                            sale.getParticipate(),
                            sale.getWinner(),
                            sale.getEndTime() != null ? Timestamp.valueOf(sale.getEndTime().toLocalDateTime()) : null,
                            sale.getUpdateTime() != null ? Timestamp.valueOf(sale.getUpdateTime().toLocalDateTime()) : null
                        );
                    } catch (Exception e) {
                        logger.warn("单条记录事务执行失败: {}-{}-{}-{}, error: {}",
                            sale.getServer(), sale.getArea(), sale.getSlot(), sale.getId(), e.getMessage());
                        status.setRollbackOnly();
                        return 0;
                    }
                });

                if (result != null && result > 0) {
                    successCount++;
                }

                if ((i + 1) % 100 == 0) {
                    logger.info("保存进度: {}/{}，成功: {}，失败: {}",
                        i + 1, housingSales.size(), successCount, errorCount);
                }

            } catch (Exception e) {
                errorCount++;
                logger.warn("保存房屋数据失败: {}-{}-{}-{}, error: {}",
                    sale.getServer(), sale.getArea(), sale.getSlot(), sale.getId(), e.getMessage());
            }

            if ((i + 1) % 50 == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        logger.info("稳健保存完成: 总共 {} 条，成功 {} 条，失败 {} 条",
            housingSales.size(), successCount, errorCount);
        return successCount;
    }

    /**
     * 筛选并通知新房屋
     */
    private void filterAndNotifyNewHouses(List<HousingSale> allHousingSales) {
        Set<Integer> targetAreas = Arrays.stream(notifyAreas.split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toSet());

        List<HousingSale> availableHouses = allHousingSales.stream()
            .filter(sale -> sale.getPurchaseType() == 1 || sale.getPurchaseType() == 2)
            .filter(sale -> targetAreas.contains(sale.getArea()))
            .collect(Collectors.toList());

        logger.info("找到 {} 套可购买房屋", availableHouses.size());

        if (!availableHouses.isEmpty()) {
            List<HousingSale> newHouses = filterNewHouses(availableHouses);

            if (!newHouses.isEmpty()) {
                logger.info("发现 {} 套新房屋，发送通知", newHouses.size());

                Map<String, List<HousingSale>> housesByServer = newHouses.stream()
                    .collect(Collectors.groupingBy(HousingSale::getServer));

                for (Map.Entry<String, List<HousingSale>> entry : housesByServer.entrySet()) {
                    sendHouseNotification(entry.getKey(), entry.getValue());
                }

                cacheNewHouses(newHouses);
            } else {
                logger.info("没有发现新房屋");
            }
        }
    }

    /**
     * 批量检查房屋是否为新房 - 包含M和L尺寸过滤
     */
    private List<HousingSale> filterNewHouses(List<HousingSale> houses) {
        if (houses.isEmpty()) {
            return houses;
        }

        try {
            List<String> cacheKeys = houses.stream()
                .map(this::getHouseCacheKey)
                .collect(Collectors.toList());

            Map<String, Boolean> cacheResults = redisUtil.hasKeys(cacheKeys);

            // 过滤出新房屋并且只保留M和L尺寸的房子
            List<HousingSale> newHouses = houses.stream()
                .filter(house -> {
                    String cacheKey = getHouseCacheKey(house);
                    return !cacheResults.getOrDefault(cacheKey, false);
                })
                .filter(house -> house.getSize() == 1 || house.getSize() == 2) // 只保留M和L尺寸的房子
                .collect(Collectors.toList());

            logger.info("批量缓存检查: 输入 {} 套，新房 {} 套 (仅M和L尺寸)", houses.size(), newHouses.size());
            return newHouses;

        } catch (Exception e) {
            logger.warn("批量缓存检查失败，降级为逐条检查: {}", e.getMessage());
            return houses.stream()
                .filter(house -> !redisUtil.hasKey(getHouseCacheKey(house)))
                .filter(house -> house.getSize() == 1 || house.getSize() == 2) // 只保留M和L尺寸的房子
                .collect(Collectors.toList());
        }
    }

    /**
     * 批量缓存新房信息
     */
    private void cacheNewHouses(List<HousingSale> newHouses) {
        if (newHouses.isEmpty()) {
            return;
        }

        try {
            Map<String, String> cacheMap = new HashMap<>();
            for (HousingSale house : newHouses) {
                String cacheKey = getHouseCacheKey(house);
                cacheMap.put(cacheKey, "1");
            }

            boolean success = redisUtil.batchSetStringKeys(cacheMap, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            if (success) {
                logger.info("成功批量缓存 {} 套新房信息", newHouses.size());
            } else {
                logger.warn("批量缓存失败，使用降级方案");
                cacheNewHousesOneByOne(newHouses);
            }

        } catch (Exception e) {
            logger.warn("批量缓存失败: {}", e.getMessage());
            cacheNewHousesOneByOne(newHouses);
        }
    }

    /**
     * 降级方案：逐条缓存
     */
    private void cacheNewHousesOneByOne(List<HousingSale> newHouses) {
        int cachedCount = 0;
        for (HousingSale house : newHouses) {
            try {
                String cacheKey = getHouseCacheKey(house);
                if (redisUtil.setWithExpire(cacheKey, "1", CACHE_EXPIRE_HOURS, TimeUnit.HOURS)) {
                    cachedCount++;
                }
            } catch (Exception e) {
                logger.warn("缓存房屋信息失败: {}", getHouseCacheKey(house), e);
            }
        }
        logger.info("逐条缓存完成: 成功 {} 套", cachedCount);
    }

    /**
     * 发送房屋通知
     */
    private void sendHouseNotification(String server, List<HousingSale> houses) {
        try {
            StringBuilder message = new StringBuilder();
            message.append("🏠 【").append(server).append("】新房源通知 (M/L尺寸)\n");
            message.append("========================\n");

            for (HousingSale house : houses) {
                String areaName = getAreaName(house.getArea());
                String purchaseType = getPurchaseTypeName(house.getPurchaseType());
                String sizeName = getSizeName(house.getSize());
                String regionTypeName = getRegionTypeName(house.getRegionType());

                message.append("📍 ").append(areaName)
                    .append(" ").append(house.getSlot() + 1).append("区")
                    .append(house.getId()).append("号\n");
                message.append("💰 价格: ").append(formatPrice(house.getPrice())).append("\n");
                message.append("📏 尺寸: ").append(sizeName).append("\n");
                message.append("🎯 方式: ").append(purchaseType).append("\n");
                message.append("👥 限制: ").append(regionTypeName).append("\n");

                if (house.getPurchaseType() == 2) {
                    if (house.getParticipate() != null) {
                        message.append("👥 参与: ").append(house.getParticipate()).append("人\n");
                    }
                    if (house.getEndTime() != null) {
                        message.append("⏰ 结束: ").append(formatTime(house.getEndTime())).append("\n");
                    }
                }
                message.append("----------------\n");
            }

            oneBotService.sendGroupMessage(message.toString(), null);

            logger.info("已发送 {} 的房屋通知，共 {} 套房屋", server, houses.size());

        } catch (Exception e) {
            logger.error("发送房屋通知失败", e);
        }
    }

    /**
     * 生成房屋缓存键
     */
    private String getHouseCacheKey(HousingSale house) {
        return HOUSING_SALE_CACHE_PREFIX +
            house.getServer() + ":" +
            house.getArea() + ":" +
            house.getSlot() + ":" +
            house.getId();
    }

    // 工具方法
    private String getAreaName(Integer area) {
        switch (area) {
            case 0: return "海雾村";
            case 1: return "薰衣草苗圃";
            case 2: return "高脚孤丘";
            case 3: return "白银乡";
            case 4: return "穹顶皓天";
            default: return "未知区域";
        }
    }

    private String getPurchaseTypeName(Integer purchaseType) {
        switch (purchaseType) {
            case 1: return "先到先得";
            case 2: return "抽签";
            default: return "未知";
        }
    }

    private String getSizeName(Integer size) {
        switch (size) {
            case 0: return "S";
            case 1: return "M";
            case 2: return "L";
            default: return "未知";
        }
    }

    private String getRegionTypeName(Integer regionType) {
        switch (regionType) {
            case 0: return "无限制";
            case 1: return "仅限部队";
            case 2: return "仅限个人";
            default: return "未知";
        }
    }

    private String formatPrice(Long price) {
        if (price >= 10000) {
            return String.format("%.1f万", price / 10000.0);
        }
        return price.toString();
    }

    private String formatTime(OffsetDateTime time) {
        return time.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }

    /**
     * 手动触发房屋数据获取（用于测试）
     */
    public void manualTriggerHousingDataFetch() {
        logger.info("手动触发房屋数据获取");
        fetchAndProcessHousingSales();
    }
}