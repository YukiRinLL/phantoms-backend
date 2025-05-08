package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.controller.PingController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        Map<String, Object> healthResponse = pingController.healthCheck().getBody();

        LocalDateTime timestamp = (LocalDateTime) healthResponse.get("timestamp");
        String databaseStatus = (String) ((Map<String, Object>) healthResponse.get("database")).get("status");
        String databaseDetails = healthResponse.get("databaseDetails").toString();
        String redisStatus = (String) healthResponse.get("redis");
        String redisDetails = healthResponse.get("redisError") != null ? (String) healthResponse.get("redisError") : "OK";
        String leancloudStatus = (String) healthResponse.get("leancloud");
        String leancloudDetails = healthResponse.get("leancloudError") != null ? (String) healthResponse.get("leancloudError") : "OK";
        String systemDetails = healthResponse.get("system").toString();

        String sql = "INSERT INTO health_check_log (timestamp, database_status, database_details, redis_status, redis_details, leancloud_status, leancloud_details, system_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, timestamp.toString(), databaseStatus, databaseDetails, redisStatus, redisDetails, leancloudStatus, leancloudDetails, systemDetails);
    }
}