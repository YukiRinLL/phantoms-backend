package com.phantoms.phantomsbackend.service.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantoms.phantomsbackend.controller.PingController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class HealthCheckScheduler {

    @Autowired
    private PingController pingController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void performHealthCheck() {
        try {
            // 获取健康检查结果
            Map<String, Object> healthResponse = pingController.healthCheck().getBody();

            // 提取健康检查结果中的关键信息
            LocalDateTime timestamp = (LocalDateTime) healthResponse.get("timestamp");

            // 数据库状态
            Map<String, Object> databaseDetails = (Map<String, Object>) healthResponse.get("databaseDetails");
            Map<String, Object> primaryDbStatus = (Map<String, Object>) databaseDetails.get("primary");
            Map<String, Object> secondaryDbStatus = (Map<String, Object>) databaseDetails.get("secondary");

            // 整合 primary 和 secondary 数据源的状态信息
            String databaseStatus = "UP"; // 默认状态
            StringBuilder databaseDetailsBuilder = new StringBuilder();
            StringBuilder connectionPoolDetailsBuilder = new StringBuilder();

            // Primary 数据源信息
            databaseDetailsBuilder.append("Primary: ").append(primaryDbStatus.get("status")).append(", ");
            connectionPoolDetailsBuilder.append("Primary: ").append(primaryDbStatus.get("poolDetails")).append(", ");
            if (!"UP".equals(primaryDbStatus.get("status"))) {
                databaseStatus = "DOWN";
            }

            // Secondary 数据源信息
            databaseDetailsBuilder.append("Secondary: ").append(secondaryDbStatus.get("status")).append(", ");
            connectionPoolDetailsBuilder.append("Secondary: ").append(secondaryDbStatus.get("poolDetails")).append(", ");
            if (!"UP".equals(secondaryDbStatus.get("status"))) {
                databaseStatus = "DOWN";
            }

            // 保留原始的数据库连接信息
            String primaryDatabaseDetails = new ObjectMapper().writeValueAsString(primaryDbStatus.get("details"));
            String secondaryDatabaseDetails = new ObjectMapper().writeValueAsString(secondaryDbStatus.get("details"));
            String databaseDetailsStr = primaryDatabaseDetails + " | " + secondaryDatabaseDetails;

            String primaryConnectionPoolDetails = new ObjectMapper().writeValueAsString(primaryDbStatus.get("poolDetails"));
            String secondaryConnectionPoolDetails = new ObjectMapper().writeValueAsString(secondaryDbStatus.get("poolDetails"));
            String connectionPoolDetailsStr = primaryConnectionPoolDetails + " | " + secondaryConnectionPoolDetails;

            // Redis 状态
            String redisStatus = (String) healthResponse.get("redis");
            String redisDetails = new ObjectMapper().writeValueAsString(healthResponse.get("redisError"));

            // LeanCloud 状态
            String leancloudStatus = (String) healthResponse.get("leancloud");
            String leancloudDetails = new ObjectMapper().writeValueAsString(healthResponse.get("leancloudError"));

            // 系统信息
            String systemDetails = new ObjectMapper().writeValueAsString(healthResponse.get("system"));

            // 将 LocalDateTime 转换为 java.sql.Timestamp
            Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);

            // 构造 SQL 插入语句
            String sql = "INSERT INTO health_check_log (timestamp, database_status, database_details, connection_pool_details, redis_status, redis_details, leancloud_status, leancloud_details, system_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, sqlTimestamp, databaseStatus, databaseDetailsStr, connectionPoolDetailsStr, redisStatus, redisDetails, leancloudStatus, leancloudDetails, systemDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}