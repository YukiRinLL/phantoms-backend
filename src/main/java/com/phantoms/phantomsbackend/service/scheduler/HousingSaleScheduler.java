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
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    // 每天0点执行
    @Scheduled(cron = "0 0 0 * * ?")
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
     * 生成房屋描述文案
     * 根据房屋特征生成吸引人的描述
     */
    private String generateBriefHouseDescription(HousingSale house) {
        String areaName = getAreaName(house.getArea());
        String sizeName = getSizeName(house.getSize());
        String purchaseType = house.getPurchaseType() == PURCHASE_TYPE_LOTTERY ? "抽" : "抢";

        return String.format("%s%s房 %s", areaName, sizeName, purchaseType);
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
//                    sendHouseNotification(entry.getKey(), entry.getValue());
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
            StringBuilder message = new StringBuilder();

            // 消息标题
            message.append("🏠 发现 ").append(server).append(" 服务器 ").append(houses.size()).append(" 套新房源\n\n");

            // 为每套房屋添加精简信息
            for (int i = 0; i < houses.size(); i++) {
                HousingSale house = houses.get(i);

                // 生成精简描述
                String briefDesc = generateBriefHouseDescription(house);
                String areaName = getAreaName(house.getArea());
                String sizeName = getSizeName(house.getSize());
                String purchaseType = house.getPurchaseType() == PURCHASE_TYPE_LOTTERY ? "抽签" : "抢购";
                String regionType = getRegionTypeName(house.getRegionType());

                // 构建单行房屋信息
                message.append(i + 1).append(". ")
                        .append(briefDesc).append(" | ")
                        .append(areaName).append(house.getSlot() + 1).append("区").append(house.getId()).append("号 | ")
                        .append(formatPrice(house.getPrice()));

                // 如果是抽签类型，显示参与人数
                if (house.getPurchaseType() == PURCHASE_TYPE_LOTTERY && house.getParticipate() != null) {
                    message.append(" | ").append(house.getParticipate()).append("人参与");
                }

                message.append("\n");
            }

            // 添加底部提示
            message.append("\n🔥 现正火热预约中！");

            // 发送单条合并消息
            oneBotService.sendGroupMessage(message.toString(), "595883141");

            logger.info("已发送 {} 服务器 {} 套房屋通知", server, houses.size());

        } catch (Exception e) {
            logger.error("发送房屋通知失败", e);
        }
    }

    /**
     * 发送房屋通知
     */
    private void sendHouseNotification(String server, List<HousingSale> houses) {
        try {
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
                oneBotService.sendGroupMessage(message.toString(), "595883141");

                logger.info("已发送 {} 的房屋通知: {}-{}-{}-{}",
                        server, areaName, house.getSlot() + 1, house.getId(), sizeName);

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
            return String.format("%.1f万", price / 10000.0);
        }
        return price.toString();
    }

    private String formatTime(OffsetDateTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }

    /**
     * 手动触发房屋数据获取（用于测试）
     */
    public void manualTriggerHousingDataFetch() {
        logger.info("手动触发房屋数据获取");
        fetchAndProcessHousingSales();
    }
}