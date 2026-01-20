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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

@Component
public class HousingSaleScheduler {

    private static final Logger logger = LoggerFactory.getLogger(HousingSaleScheduler.class);

    private static final String HOUSING_SALE_CACHE_PREFIX = "housing_sale:";
    private static final long CACHE_EXPIRE_HOURS = 72;

    // 房屋大小枚举
    private static final int SIZE_S = 0;
    private static final int SIZE_M = 1;
    private static final int SIZE_L = 2;

    // 区域枚举
    private static final int AREA_MIST = 0;
    private static final int AREA_LAVENDER_BEDS = 1;
    private static final int AREA_GOBLET = 2;
    private static final int AREA_SHIROGANE = 3;
    private static final int AREA_EMPYREUM = 4;

    // 购买类型枚举
    private static final int PURCHASE_TYPE_FCFS = 1;
    private static final int PURCHASE_TYPE_LOTTERY = 2;

    // 区域类型枚举
    private static final int REGION_TYPE_PERSONAL = 2;

    private static final Map<String, String> SERVER_NAME_MAP = Map.of(
            "1121", "拂晓之间",
            "1081", "神意之地"
    );

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

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    @Value("${napcat.phantom-group-id}")
    private String phantomGroupId;

    // UTC+8每天23:05执行
    @Scheduled(cron = "0 5 15 * * ?")
//    @Scheduled(fixedRate = 60000) // 每分钟执行一次
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
     * 计算推测的抽签截止时间
     * 基于FF14房屋抽签规则：通常持续4天，在特定时间结束
     */
    private OffsetDateTime calculateEstimatedEndTime(HousingSale house) {
        try {
            // 获取首次出现时间
            OffsetDateTime firstSeen = house.getFirstSeen();
            if (firstSeen == null) {
                firstSeen = OffsetDateTime.now();
            }

            // FF14抽签周期通常为4天
            int lotteryDays = 4;

            // 计算基础结束时间（首次出现时间 + 4天）
            OffsetDateTime baseEndTime = firstSeen.plusDays(lotteryDays);

            // 调整到特定的结束时间点（通常是晚上23:00）
            LocalDateTime endLocalDateTime = baseEndTime.toLocalDate()
                    .atTime(23, 0, 0);

            // 转换为OffsetDateTime，保持相同的时区偏移
            return OffsetDateTime.of(endLocalDateTime, baseEndTime.getOffset());

        } catch (Exception e) {
            logger.warn("计算推测截止时间失败，使用默认值", e);
            return OffsetDateTime.now().plusDays(4).withHour(23).withMinute(0).withSecond(0);
        }
    }

    /**
     * 生成房屋描述文案
     * 根据房屋特征生成吸引人的描述
     */
    private String generateHouseDescription(HousingSale house) {
        List<String> descriptions = new ArrayList<>();

        // 根据区域生成特色描述
        switch (house.getArea()) {
            case AREA_MIST:
                descriptions.addAll(Arrays.asList("海景别墅", "私家海滨", "无敌海景"));
                break;
            case AREA_LAVENDER_BEDS:
                descriptions.addAll(Arrays.asList("林间雅居", "薰衣草庭院", "静谧森林"));
                break;
            case AREA_GOBLET:
                descriptions.addAll(Arrays.asList("山景豪宅", "独家山景", "高原风光"));
                break;
            case AREA_SHIROGANE:
                descriptions.addAll(Arrays.asList("日式庭院", "东方风情", "温泉旁"));
                break;
            case AREA_EMPYREUM:
                descriptions.addAll(Arrays.asList("天空之城", "云顶豪宅", "伊修加德"));
                break;
        }

        // 根据尺寸添加描述
        if (house.getSize() == SIZE_M) {
            descriptions.add("温馨公馆");
        } else if (house.getSize() == SIZE_L) {
            descriptions.addAll(Arrays.asList("豪华别墅", "尊贵府邸"));
        }

        // 随机选择一个描述
        if (!descriptions.isEmpty()) {
            Random random = new Random();
            return descriptions.get(random.nextInt(descriptions.size()));
        }

        return "优质房产";
    }

    /**
     * 房屋数据保存方法 - 优先批量保存，失败则降级为逐个保存
     */
    private int robustSaveHousingSales(List<HousingSale> housingSales) {
        if (housingSales.isEmpty()) {
            return 0;
        }

        logger.info("开始保存 {} 条房屋数据", housingSales.size());

        // 首先尝试批量保存
        int batchSaved = batchSaveHousingSales(housingSales);
        if (batchSaved == housingSales.size()) {
            logger.info("批量保存成功，保存了 {} 条数据", batchSaved);
            return batchSaved;
        }

        // 如果批量保存不完整，降级为逐个保存
        logger.warn("批量保存不完整 ({} / {})，降级为逐个保存", batchSaved, housingSales.size());
        int individualSaved = individualSaveHousingSales(housingSales);

        logger.info("保存完成: 总共 {} 条，批量保存 {} 条，逐个保存 {} 条",
                housingSales.size(), batchSaved, individualSaved);
        return batchSaved + individualSaved;
    }

    /**
     * 批量保存房屋数据
     */
    private int batchSaveHousingSales(List<HousingSale> housingSales) {
        if (housingSales.isEmpty()) {
            return 0;
        }

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

        try {
            int[] results = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    HousingSale sale = housingSales.get(i);
                    ps.setString(1, sale.getServer());
                    ps.setInt(2, sale.getArea());
                    ps.setInt(3, sale.getSlot());
                    ps.setInt(4, sale.getId());
                    ps.setLong(5, sale.getPrice());
                    ps.setInt(6, sale.getSize());

                    if (sale.getFirstSeen() != null) {
                        ps.setTimestamp(7, Timestamp.valueOf(sale.getFirstSeen().toLocalDateTime()));
                    } else {
                        ps.setNull(7, java.sql.Types.TIMESTAMP);
                    }

                    if (sale.getLastSeen() != null) {
                        ps.setTimestamp(8, Timestamp.valueOf(sale.getLastSeen().toLocalDateTime()));
                    } else {
                        ps.setNull(8, java.sql.Types.TIMESTAMP);
                    }

                    ps.setInt(9, sale.getPurchaseType());
                    ps.setInt(10, sale.getRegionType());
                    ps.setInt(11, sale.getState());
                    ps.setInt(12, sale.getParticipate());
                    ps.setString(13, sale.getWinner());

                    if (sale.getEndTime() != null) {
                        ps.setTimestamp(14, Timestamp.valueOf(sale.getEndTime().toLocalDateTime()));
                    } else {
                        ps.setNull(14, java.sql.Types.TIMESTAMP);
                    }

                    if (sale.getUpdateTime() != null) {
                        ps.setTimestamp(15, Timestamp.valueOf(sale.getUpdateTime().toLocalDateTime()));
                    } else {
                        ps.setNull(15, java.sql.Types.TIMESTAMP);
                    }
                }

                @Override
                public int getBatchSize() {
                    return housingSales.size();
                }
            });

            int successCount = 0;
            for (int result : results) {
                if (result >= 0) { // 0或1都表示成功（0表示没有变化，1表示插入或更新）
                    successCount++;
                }
            }

            logger.info("批量保存结果: 总共 {} 条，成功 {} 条", housingSales.size(), successCount);
            return successCount;

        } catch (Exception e) {
            logger.error("批量保存失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 逐个保存房屋数据（降级方案）
     */
    private int individualSaveHousingSales(List<HousingSale> housingSales) {
        if (housingSales.isEmpty()) {
            return 0;
        }

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
                    logger.info("逐个保存进度: {}/{}，成功: {}，失败: {}",
                            i + 1, housingSales.size(), successCount, errorCount);
                }

            } catch (Exception e) {
                errorCount++;
                logger.warn("保存房屋数据失败: {}-{}-{}-{}, error: {}",
                        sale.getServer(), sale.getArea(), sale.getSlot(), sale.getId(), e.getMessage());
            }

            // 添加小延迟，避免对数据库造成过大压力
            if ((i + 1) % 50 == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        logger.info("逐个保存完成: 总共 {} 条，成功 {} 条，失败 {} 条",
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
                .filter(sale -> sale.getPurchaseType() == PURCHASE_TYPE_FCFS || sale.getPurchaseType() == PURCHASE_TYPE_LOTTERY)
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
                    sendBriefHouseNotification(entry.getKey(), entry.getValue());
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
                    .filter(house -> house.getSize() == SIZE_M || house.getSize() == SIZE_L) // 只保留M和L尺寸的房子
                    .collect(Collectors.toList());

            logger.info("批量缓存检查: 输入 {} 套，新房 {} 套 (仅M和L尺寸)", houses.size(), newHouses.size());
            return newHouses;

        } catch (Exception e) {
            logger.warn("批量缓存检查失败，降级为逐条检查: {}", e.getMessage());
            return houses.stream()
                    .filter(house -> !redisUtil.hasKey(getHouseCacheKey(house)))
                    .filter(house -> house.getSize() == SIZE_M || house.getSize() == SIZE_L) // 只保留M和L尺寸的房子
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
     * 发送房屋通知 - 单条消息包含所有房屋信息
     */
    private void sendBriefHouseNotification(String server, List<HousingSale> houses) {
        try {
            // 获取服务器名称
            String serverName = SERVER_NAME_MAP.getOrDefault(server, server);
            
            // 优先尝试发送表格图片
            try {
                // 生成表格图片
                String image = generateHousingTableImage(server, houses);
                
                // 发送群图片
                oneBotService.sendGroupImage(image, phantomGroupId);
                
                logger.info("已发送 {} 服务器 {} 套房屋表格图片通知", server, houses.size());
                return;
            } catch (Exception e) {
                logger.warn("发送表格图片失败，降级为文本消息: {}", e.getMessage());
            }
            
            // 如果图片发送失败，降级为文本消息
            StringBuilder message = new StringBuilder();
            
            // 消息标题
            message.append("🏠 发现 ").append(serverName).append(" 服务器 ").append(houses.size()).append(" 套新房源\n\n");

            // 为每套房屋添加精简信息
            for (int i = 0; i < houses.size(); i++) {
                HousingSale house = houses.get(i);

                // 生成精简描述
                String areaName = getAreaName(house.getArea());
                String sizeName = getSizeName(house.getSize());
                String purchaseType = house.getPurchaseType() == PURCHASE_TYPE_LOTTERY ? "抽签" : "抢购";
                String regionType = getRegionTypeName(house.getRegionType());

                // 计算推测截止时间
                OffsetDateTime estimatedEndTime = calculateEstimatedEndTime(house);

                // 构建单行房屋信息
                message.append(i + 1).append(". ")
                        .append(sizeName).append(" | ")
                        .append(areaName).append(house.getSlot() + 1).append("区").append(house.getId()).append("号 | ")
                        .append(formatPrice(house.getPrice())).append(" | ")
                        .append(regionType).append(" | ")
                        .append(formatTime(estimatedEndTime)).append("截止");

                // 如果是抽签类型，显示参与人数
                if (house.getPurchaseType() == PURCHASE_TYPE_LOTTERY && house.getParticipate() != null) {
                    message.append(" | ").append(house.getParticipate()).append("参与");
                }

                message.append("\n");
            }

            // 发送单条合并文本消息
            oneBotService.sendGroupMessage(message.toString(), phantomGroupId);

            logger.info("已发送 {} 服务器 {} 套房屋文本通知", server, houses.size());

        } catch (Exception e) {
            logger.error("发送房屋通知失败", e);
        }
    }

    /**
     * 发送房屋通知
     */
    private void sendHouseNotification(String server, List<HousingSale> houses) {
        try {
            // 获取服务器名称
            String serverName = SERVER_NAME_MAP.getOrDefault(server, server);

            for (HousingSale house : houses) {
                StringBuilder message = new StringBuilder();

                // 生成房屋描述
                String description = generateHouseDescription(house);
                String areaName = getAreaName(house.getArea());
                String sizeName = getSizeName(house.getSize());
                String purchaseTypeName = getPurchaseTypeName(house.getPurchaseType());
                String regionTypeName = getRegionTypeName(house.getRegionType());

                // 按照指定格式构建消息
                message.append("🏠 ").append(description).append("\n");
                message.append("📏 尺寸: ").append(sizeName).append("\n");
                message.append("📍 ").append(areaName).append(" ").append(house.getSlot() + 1).append("区")
                        .append(house.getId()).append("号\n");
                message.append("🎯 方式: ").append(purchaseTypeName).append("\n");
                message.append("👤 限制: ").append(regionTypeName).append("\n");

                // 参与人数（仅抽签类型显示）
                if (house.getPurchaseType() == PURCHASE_TYPE_LOTTERY) {
                    message.append("👥 参与: ").append(house.getParticipate() != null ? house.getParticipate() : 0).append("人\n");
                }

                message.append("💰 价格: ").append(formatPrice(house.getPrice())).append("\n");

                // 购买方式和截止时间（仅抽签类型显示）
                if (house.getPurchaseType() == PURCHASE_TYPE_LOTTERY) {
                    message.append("🎫 购买方式: 抽签\n");

                    // 计算并显示推测截止时间
                    OffsetDateTime estimatedEndTime = calculateEstimatedEndTime(house);
                    message.append("⏰ ").append(formatTime(estimatedEndTime)).append(" 截止\n");
                } else {
                    message.append("🎫 购买方式: 先到先得\n");
                }

                message.append("\n🔥 现正火热预约中！\n");

                // 发送单条房屋通知
                oneBotService.sendGroupMessage(message.toString(), defaultGroupId);

                logger.info("已发送 {} 的房屋通知: {}-{}-{}-{}",
                        serverName, areaName, house.getSlot() + 1, house.getId(), sizeName);

                // 添加间隔，避免消息过于密集
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

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
            case AREA_MIST: return "海雾村";
            case AREA_LAVENDER_BEDS: return "薰衣草苗圃";
            case AREA_GOBLET: return "高脚孤丘";
            case AREA_SHIROGANE: return "白银乡";
            case AREA_EMPYREUM: return "穹顶皓天";
            default: return "未知区域";
        }
    }

    private String getPurchaseTypeName(Integer purchaseType) {
        switch (purchaseType) {
            case PURCHASE_TYPE_FCFS: return "先到先得";
            case PURCHASE_TYPE_LOTTERY: return "抽签";
            default: return "未知";
        }
    }

    private String getSizeName(Integer size) {
        switch (size) {
            case SIZE_S: return "S";
            case SIZE_M: return "M";
            case SIZE_L: return "L";
            default: return "未知";
        }
    }

    private String getRegionTypeName(Integer regionType) {
        switch (regionType) {
            case 0: return "无限制";
            case 1: return "仅限部队";
            case REGION_TYPE_PERSONAL: return "仅限个人";
            default: return "未知";
        }
    }

    private String formatPrice(Long price) {
        if (price >= 10000) {
            return String.format("%.1fw", price / 10000.0);
        }
        return price.toString();
    }

    private String formatTime(OffsetDateTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }

    /**
     * 生成房屋信息表格图片
     */
    private String generateHousingTableImage(String server, List<HousingSale> houses) throws IOException {
        // 获取服务器名称
        String serverName = SERVER_NAME_MAP.getOrDefault(server, server);
        
        // 定义表头和对应的数据获取函数
        String[] headers = {"序号", "尺寸", "位置", "价格", "购买方式", "限制", "参与人数", "截止时间", "首次发现"};
        int cols = headers.length;
        int rows = houses.size() + 2; // 表头 + 数据 + 标题
        
        // 设置字体
        Font headerFont = new Font("黑体", Font.BOLD, 14);
        Font dataFont = new Font("黑体", Font.PLAIN, 12);
        Font titleFont = new Font("黑体", Font.BOLD, 24);
        
        // 计算动态列宽
        int[] columnWidths = calculateColumnWidths(houses, headers, headerFont, dataFont);
        
        // 创建临时图像获取标题字体信息
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2d = tempImage.createGraphics();
        tempG2d.setFont(titleFont);
        String title = "🏠 " + serverName + " 服务器新房源信息";
        FontMetrics titleMetrics = tempG2d.getFontMetrics();
        tempG2d.dispose();
        
        // 图片尺寸设置
        int cellHeight = 35; // 增加行高以适应更多内容
        int padding = 20;
        int titleSpacing = cellHeight / 2; // 标题与表格的间距
        int imageWidth = Arrays.stream(columnWidths).sum() + padding * 2;
        int imageHeight = titleMetrics.getHeight() + titleSpacing + cellHeight * (rows - 1) + padding * 2;
        
        // 创建图片
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置背景色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        
        // 设置边框和表头颜色
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        
        // 绘制标题
        g2d.setFont(titleFont);
        int titleX = (imageWidth - titleMetrics.stringWidth(title)) / 2;
        int titleY = padding + titleMetrics.getHeight();
        g2d.drawString(title, titleX, titleY);
        
        // 绘制表头
        g2d.setFont(headerFont);
        int headerY = titleY + cellHeight / 2; // 增加标题与表格的间距
        int currentX = padding;
        
        // 表头背景色
        Color headerBgColor = new Color(240, 240, 240);
        
        for (int i = 0; i < cols; i++) {
            int width = columnWidths[i];
            // 绘制表头背景
            g2d.setColor(headerBgColor);
            g2d.fillRect(currentX + 1, headerY + 1, width - 1, cellHeight - 1);
            
            // 绘制表头边框
            g2d.setColor(Color.BLACK);
            g2d.drawRect(currentX, headerY, width, cellHeight);
            
            // 绘制表头文本
            String header = headers[i];
            FontMetrics metrics = g2d.getFontMetrics();
            int textX = currentX + (width - metrics.stringWidth(header)) / 2;
            int textY = headerY + (cellHeight + metrics.getHeight()) / 2 - metrics.getDescent();
            g2d.drawString(header, textX, textY);
            
            currentX += width;
        }
        
        // 绘制数据行
        g2d.setFont(dataFont);
        
        for (int row = 0; row < houses.size(); row++) {
            HousingSale house = houses.get(row);
            int rowY = headerY + (row + 1) * cellHeight;
            currentX = padding;
            
            // 设置行背景色（交替行）
            if (row % 2 == 1) {
                g2d.setColor(new Color(250, 250, 250));
                int totalWidth = Arrays.stream(columnWidths).sum();
                g2d.fillRect(currentX + 1, rowY + 1, totalWidth - 1, cellHeight - 1);
                g2d.setColor(Color.BLACK); // 恢复边框颜色
            }
            
            // 1. 序号
            String serial = String.valueOf(row + 1);
            drawTableCell(g2d, currentX, rowY, columnWidths[0], cellHeight, serial);
            currentX += columnWidths[0];
            
            // 2. 尺寸
            String size = getSizeName(house.getSize());
            drawTableCell(g2d, currentX, rowY, columnWidths[1], cellHeight, size);
            currentX += columnWidths[1];
            
            // 3. 位置
            String area = getAreaName(house.getArea());
            String position = area + (house.getSlot() + 1) + "区" + house.getId() + "号";
            drawTableCell(g2d, currentX, rowY, columnWidths[2], cellHeight, position);
            currentX += columnWidths[2];
            
            // 4. 价格
            String price = formatPrice(house.getPrice());
            drawTableCell(g2d, currentX, rowY, columnWidths[3], cellHeight, price);
            currentX += columnWidths[3];
            
            // 5. 购买方式
            String purchaseType = getPurchaseTypeName(house.getPurchaseType());
            drawTableCell(g2d, currentX, rowY, columnWidths[4], cellHeight, purchaseType);
            currentX += columnWidths[4];
            
            // 6. 限制
            String regionType = getRegionTypeName(house.getRegionType());
            drawTableCell(g2d, currentX, rowY, columnWidths[5], cellHeight, regionType);
            currentX += columnWidths[5];
            
            // 7. 参与人数
            String participate = house.getPurchaseType() == PURCHASE_TYPE_LOTTERY && house.getParticipate() != null ? 
                                String.valueOf(house.getParticipate()) : "-";
            drawTableCell(g2d, currentX, rowY, columnWidths[6], cellHeight, participate);
            currentX += columnWidths[6];
            
            // 8. 截止时间
            OffsetDateTime estimatedEndTime = calculateEstimatedEndTime(house);
            String endTime = formatTime(estimatedEndTime) + "截止";
            drawTableCell(g2d, currentX, rowY, columnWidths[7], cellHeight, endTime);
            currentX += columnWidths[7];
            
            // 9. 首次发现
            String firstSeen = house.getFirstSeen() != null ? 
                              formatTime(house.getFirstSeen()) : "-";
            drawTableCell(g2d, currentX, rowY, columnWidths[8], cellHeight, firstSeen);
        }
        
        // 释放资源
        g2d.dispose();
        
        // 将图片转换为base64编码
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        
        // 使用Base64编码
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        // 添加base64://前缀，符合Napcat的要求
        String result = "base64://" + base64Image;
        
        logger.info("生成房屋表格图片base64编码成功，长度: {}", result.length());
        
        return result;
    }
    
    /**
     * 计算动态列宽
     */
    private int[] calculateColumnWidths(List<HousingSale> houses, String[] headers, Font headerFont, Font dataFont) {
        int cols = headers.length;
        int[] columnWidths = new int[cols];
        
        // 创建临时图像用于测量文本宽度
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        
        // 计算表头所需宽度
        g2d.setFont(headerFont);
        for (int i = 0; i < cols; i++) {
            int width = g2d.getFontMetrics().stringWidth(headers[i]) + 20; // 增加20像素边距
            columnWidths[i] = Math.max(columnWidths[i], width);
        }
        
        // 计算数据所需宽度
        g2d.setFont(dataFont);
        for (HousingSale house : houses) {
            // 序号
            String serial = String.valueOf(houses.indexOf(house) + 1);
            int width = g2d.getFontMetrics().stringWidth(serial) + 20;
            columnWidths[0] = Math.max(columnWidths[0], width);
            
            // 尺寸
            String size = getSizeName(house.getSize());
            width = g2d.getFontMetrics().stringWidth(size) + 20;
            columnWidths[1] = Math.max(columnWidths[1], width);
            
            // 位置
            String area = getAreaName(house.getArea());
            String position = area + (house.getSlot() + 1) + "区" + house.getId() + "号";
            width = g2d.getFontMetrics().stringWidth(position) + 20;
            columnWidths[2] = Math.max(columnWidths[2], width);
            
            // 价格
            String price = formatPrice(house.getPrice());
            width = g2d.getFontMetrics().stringWidth(price) + 20;
            columnWidths[3] = Math.max(columnWidths[3], width);
            
            // 购买方式
            String purchaseType = getPurchaseTypeName(house.getPurchaseType());
            width = g2d.getFontMetrics().stringWidth(purchaseType) + 20;
            columnWidths[4] = Math.max(columnWidths[4], width);
            
            // 限制
            String regionType = getRegionTypeName(house.getRegionType());
            width = g2d.getFontMetrics().stringWidth(regionType) + 20;
            columnWidths[5] = Math.max(columnWidths[5], width);
            
            // 参与人数
            String participate = house.getPurchaseType() == PURCHASE_TYPE_LOTTERY && house.getParticipate() != null ? 
                                String.valueOf(house.getParticipate()) : "-";
            width = g2d.getFontMetrics().stringWidth(participate) + 20;
            columnWidths[6] = Math.max(columnWidths[6], width);
            
            // 截止时间
            OffsetDateTime estimatedEndTime = calculateEstimatedEndTime(house);
            String endTime = formatTime(estimatedEndTime) + "截止";
            width = g2d.getFontMetrics().stringWidth(endTime) + 20;
            columnWidths[7] = Math.max(columnWidths[7], width);
            
            // 首次发现
            String firstSeen = house.getFirstSeen() != null ? 
                              formatTime(house.getFirstSeen()) : "-";
            width = g2d.getFontMetrics().stringWidth(firstSeen) + 20;
            columnWidths[8] = Math.max(columnWidths[8], width);
        }
        
        // 设置最小列宽，确保表格美观
        for (int i = 0; i < cols; i++) {
            columnWidths[i] = Math.max(columnWidths[i], 80); // 最小列宽80像素
        }
        
        // 释放资源
        g2d.dispose();
        
        return columnWidths;
    }
    
    /**
     * 绘制表格单元格
     */
    private void drawTableCell(Graphics2D g2d, int x, int y, int width, int height, String text) {
        // 绘制边框
        g2d.drawRect(x, y, width, height);
        
        // 绘制文本
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        int textY = y + (height + metrics.getHeight()) / 2 - metrics.getDescent();
        g2d.drawString(text, textX, textY);
    }
    
    /**
     * 手动触发房屋数据获取（用于测试）
     */
    public void manualTriggerHousingDataFetch() {
        logger.info("手动触发房屋数据获取");
        fetchAndProcessHousingSales();
    }
}