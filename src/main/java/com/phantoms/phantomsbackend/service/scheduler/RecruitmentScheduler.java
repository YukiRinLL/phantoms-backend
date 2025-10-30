package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.LittlenightmareClient;
import com.phantoms.phantomsbackend.common.utils.RedisUtil;
import com.phantoms.phantomsbackend.pojo.entity.RecruitmentResponse;
import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.repository.primary.RecruitmentRepository;
import com.phantoms.phantomsbackend.service.OneBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RecruitmentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecruitmentScheduler.class);

    // Redis 缓存键前缀
    private static final String RECRUITMENT_CACHE_PREFIX = "recruitment:";
    // 缓存过期时间（24小时）
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Autowired
    private OneBotService oneBotService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Scheduled(fixedRate = 300000) // 每300秒执行一次
    @Transactional
    public void fetchAndFilterRecruitments() {
        try {
            List<RecruitmentResponse> allResponses = LittlenightmareClient.fetchAllRecruitmentListings(
                100, // perPage
                null, // category
                null, // world
                null, // search
                null, // datacenter
                null, // jobs
                null  // duties
            );

            // Flatten the list of responses into a single list of recruitments
            List<Recruitment> allRecruitments = allResponses.stream()
                .flatMap(response -> response.getData().stream())
                .collect(Collectors.toList());

            // 使用批量保存
            int savedCount = batchSaveWithPostgreSQL(allRecruitments);

            logger.info("Successfully fetched {} recruitments, attempted to save {} recruitments",
                allRecruitments.size(), allRecruitments.size());

//            filterAndNotify(allRecruitments);
        } catch (Exception e) {
            logger.error("Failed to fetch and filter recruitments", e);
        }
    }

    /**
     * 使用 PostgreSQL 的 ON CONFLICT 进行批量插入
     */
    private int batchSaveWithPostgreSQL(List<Recruitment> recruitments) {
        if (recruitments.isEmpty()) {
            return 0;
        }

        String sql = "INSERT INTO recruitments " +
            "(id, name, description, created_world, created_world_id, home_world, home_world_id, " +
            "category, category_id, duty, min_item_level, slots_filled, slots_available, " +
            "time_left, updated_at, is_cross_world, datacenter) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (id) DO NOTHING";

        int[] results = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Recruitment rec = recruitments.get(i);
                ps.setInt(1, rec.getId());
                ps.setString(2, rec.getName());
                ps.setString(3, rec.getDescription());
                ps.setString(4, rec.getCreatedWorld());
                ps.setInt(5, rec.getCreatedWorldId());
                ps.setString(6, rec.getHomeWorld());
                ps.setInt(7, rec.getHomeWorldId());
                ps.setString(8, rec.getCategory());
                ps.setInt(9, rec.getCategoryId());
                ps.setString(10, rec.getDuty());
                ps.setInt(11, rec.getMinItemLevel());
                ps.setInt(12, rec.getSlotsFilled());
                ps.setInt(13, rec.getSlotsAvailable());
                ps.setDouble(14, rec.getTimeLeft());

                // 处理 OffsetDateTime
                if (rec.getUpdatedAt() != null) {
                    ps.setTimestamp(15, Timestamp.valueOf(rec.getUpdatedAt().toLocalDateTime()));
                } else {
                    ps.setTimestamp(15, null);
                }

                ps.setBoolean(16, rec.isCrossWorld());
                ps.setString(17, rec.getDatacenter());
            }

            @Override
            public int getBatchSize() {
                return recruitments.size();
            }
        });

        return results.length;
    }

    /**
     * 筛选并通知，使用 Redis 缓存避免重复发送
     */
    private void filterAndNotify(List<Recruitment> allRecruitments) {
        List<Recruitment> filteredRecruitments = allRecruitments.stream()
            .filter(recruitment -> recruitment.getDescription() != null &&
                recruitment.getDescription().contains("HQ"))
            .collect(Collectors.toList());

        if (!filteredRecruitments.isEmpty()) {
            logger.info("Found {} filtered recruitments before cache check", filteredRecruitments.size());

            // 过滤出未在缓存中的招募信息
            List<Recruitment> newRecruitments = filteredRecruitments.stream()
                .filter(recruitment -> !isRecruitmentInCache(recruitment.getId()))
                .collect(Collectors.toList());

            if (!newRecruitments.isEmpty()) {
                logger.info("Sending notifications for {} new recruitments", newRecruitments.size());

                newRecruitments.forEach(recruitment -> {
                    try {
                        // 发送消息
                        oneBotService.sendGroupMessage(
                            "[招募信息] (" + recruitment.getName() + ")\n" +
                                "Category: " + recruitment.getCategory() + "\n" +
                                "Duty: " + recruitment.getDuty() + "\n" +
                                recruitment.getDescription() + "\n" +
                                "HomeWorld: " + recruitment.getHomeWorld() + "\n" +
                                "Posted: " + recruitment.getDatacenter() + "-" + recruitment.getCreatedWorld() + "\n" +
                                "UpdatedAt: " + recruitment.getUpdatedAt() + "\n" +
                                "TimeLeft: " + recruitment.getTimeLeft() + "s\n",
                            null
                        );

                        // 将已发送的招募信息加入缓存
                        cacheRecruitment(recruitment.getId());

                    } catch (Exception e) {
                        logger.error("Failed to send notification for recruitment {}", recruitment.getId(), e);
                    }
                });
            } else {
                logger.info("No new recruitments found after cache check");
            }
        } else {
            logger.info("No filtered recruitments found");
        }
    }

    /**
     * 检查招募信息是否在缓存中
     */
    private boolean isRecruitmentInCache(Integer recruitmentId) {
        try {
            String cacheKey = getCacheKey(recruitmentId);
            return redisUtil.hasKey(cacheKey);
        } catch (Exception e) {
            logger.warn("Failed to check cache for recruitment {}, treating as not cached", recruitmentId, e);
            return false; // 如果缓存检查失败，当作不在缓存中处理
        }
    }

    /**
     * 将招募信息加入缓存
     */
    private void cacheRecruitment(Integer recruitmentId) {
        try {
            String cacheKey = getCacheKey(recruitmentId);
            // 使用 "1" 作为值，我们只关心键是否存在
            redisUtil.setWithExpire(cacheKey, "1", CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            logger.debug("Cached recruitment {} with key: {}", recruitmentId, cacheKey);
        } catch (Exception e) {
            logger.warn("Failed to cache recruitment {}", recruitmentId, e);
        }
    }

    /**
     * 生成缓存键
     */
    private String getCacheKey(Integer recruitmentId) {
        return RECRUITMENT_CACHE_PREFIX + recruitmentId;
    }

    /**
     * 简化版批量保存 - 只使用最基本的功能（修正类型问题）
     */
    private int simpleBatchSave(List<Recruitment> recruitments) {
        if (recruitments.isEmpty()) {
            return 0;
        }

        // 先批量检查已存在的ID - 修正类型转换问题
        List<Long> existingIds = recruitmentRepository.findAllById(
                recruitments.stream()
                    .map(rec -> (long) rec.getId())  // 将 int 转换为 Long
                    .collect(Collectors.toList())
            ).stream()
            .map(Recruitment::getId)
            .map(Integer::longValue)  // 将 Integer 转换为 Long
            .collect(Collectors.toList());

        // 过滤出新记录
        List<Recruitment> newRecruitments = recruitments.stream()
            .filter(rec -> !existingIds.contains((long) rec.getId()))  // 比较时也转换为 Long
            .collect(Collectors.toList());

        if (!newRecruitments.isEmpty()) {
            recruitmentRepository.saveAll(newRecruitments);
            recruitmentRepository.flush(); // 确保立即提交
        }

        return newRecruitments.size();
    }

    /**
     * 降级方案：逐条保存（修正类型问题）
     */
    private int saveOneByOne(List<Recruitment> recruitments) {
        int successCount = 0;
        for (Recruitment recruitment : recruitments) {
            try {
                // 检查是否已存在 - 将 int 转换为 Long
                if (!recruitmentRepository.existsById((long) recruitment.getId())) {
                    recruitmentRepository.save(recruitment);
                    successCount++;
                }
            } catch (Exception e) {
                logger.warn("Failed to save recruitment {}: {}", recruitment.getId(), e.getMessage());
            }
        }
        return successCount;
    }

    /**
     * 替代方案：使用自定义查询检查存在性
     */
    private int batchSaveWithCustomCheck(List<Recruitment> recruitments) {
        if (recruitments.isEmpty()) {
            return 0;
        }

        // 构建ID列表用于查询
        List<Integer> ids = recruitments.stream()
            .map(Recruitment::getId)
            .collect(Collectors.toList());

        // 使用自定义查询检查存在性
        String checkSql = "SELECT id FROM recruitments WHERE id IN (" +
            ids.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";

        List<Integer> existingIds = jdbcTemplate.queryForList(checkSql, ids.toArray(), Integer.class);

        // 过滤出新记录
        List<Recruitment> newRecruitments = recruitments.stream()
            .filter(rec -> !existingIds.contains(rec.getId()))
            .collect(Collectors.toList());

        if (!newRecruitments.isEmpty()) {
            // 使用 PostgreSQL 批量插入新记录
            return batchSaveWithPostgreSQL(newRecruitments);
        }

        return 0;
    }
}