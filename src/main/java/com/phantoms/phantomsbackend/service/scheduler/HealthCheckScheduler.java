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
            String databaseStatus = (String) healthResponse.get("database");
            String databaseDetails = new ObjectMapper().writeValueAsString(healthResponse.get("databaseDetails"));
            String connectionPoolDetails = new ObjectMapper().writeValueAsString(healthResponse.get("connectionPoolDetails"));
            String redisStatus = (String) healthResponse.get("redis");
            String redisDetails = new ObjectMapper().writeValueAsString(healthResponse.get("redisError"));
            String leancloudStatus = (String) healthResponse.get("leancloud");
            String leancloudDetails = new ObjectMapper().writeValueAsString(healthResponse.get("leancloudError"));
            String systemDetails = new ObjectMapper().writeValueAsString(healthResponse.get("system"));

            // 将 LocalDateTime 转换为 java.sql.Timestamp
            Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);

            // 构造 SQL 插入语句
            String sql = "INSERT INTO health_check_log (timestamp, database_status, database_details, connection_pool_details, redis_status, redis_details, leancloud_status, leancloud_details, system_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, sqlTimestamp, databaseStatus, databaseDetails, connectionPoolDetails, redisStatus, redisDetails, leancloudStatus, leancloudDetails, systemDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}