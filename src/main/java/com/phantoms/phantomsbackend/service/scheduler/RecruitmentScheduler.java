package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.LittlenightmareClient;
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
import java.util.stream.Collectors;

@Component
public class RecruitmentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecruitmentScheduler.class);

    @Autowired
    private OneBotService oneBotService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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


    // 原有的筛选逻辑（如果需要可以取消注释）
    private void filterAndNotify(List<Recruitment> allRecruitments) {
        List<Recruitment> filteredRecruitments = allRecruitments.stream()
                .filter(recruitment -> recruitment.getDescription() != null &&
                                      recruitment.getDescription().contains("HQ"))
                .collect(Collectors.toList());

        if (!filteredRecruitments.isEmpty()) {
            logger.info("Found {} filtered recruitments", filteredRecruitments.size());

            filteredRecruitments.forEach(recruitment -> {
                try {
                    oneBotService.sendGroupMessageWithDefaultGroup(
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
                } catch (Exception e) {
                    logger.error("Failed to send notification for recruitment {}", recruitment.getId(), e);
                }
            });
        } else {
            logger.info("No filtered recruitments found");
        }
    }

}