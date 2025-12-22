package com.phantoms.phantomsbackend.controller;

//import com.phantoms.phantomsbackend.common.utils.RisingStonesLoginTool;
//import com.phantoms.phantomsbackend.service.scheduler.DaoYuKeyMonitorScheduler;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.phantoms.phantomsbackend.common.utils.LeanCloudUtils;

@RestController
@Tag(name = "Ping Controller", description = "Provides health check and ping endpoints")
public class PingController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

//    @Autowired
//    private RisingStonesLoginTool risingStonesLoginTool;

//    @Autowired
//    private DaoYuKeyMonitorScheduler daoYuKeyMonitorScheduler;

    @Value("${app.version:unknown}")
    private String appVersion;

    @Value("${spring.datasource.primary.url}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryUsername;

    @Value("${spring.datasource.secondary.url}")
    private String secondaryUrl;

    @Value("${spring.datasource.secondary.username}")
    private String secondaryUsername;

    @GetMapping("/ping")
    @Operation(summary = "Ping endpoint", description = "Returns a simple ping response to check if the server is up.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ping successful",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            })
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Ping successful");
        response.put("details", getSystemDetails());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health Check endpoint", description = "Returns the health status of the application, including database, Redis, and LeanCloud status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Health status",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("timestamp", LocalDateTime.now());

        // 数据库状态
        Map<String, Object> databaseDetails = new HashMap<>();
        databaseDetails.put("primary", checkDataSource(primaryDataSource, primaryUrl, primaryUsername));
        databaseDetails.put("secondary", checkDataSource(secondaryDataSource, secondaryUrl, secondaryUsername));
        healthResponse.put("database", "UP"); // 总体数据库状态
        healthResponse.put("databaseDetails", databaseDetails);

        // Redis 状态
        boolean isRedisConnected = false;
        try (org.springframework.data.redis.connection.RedisConnection connection = redisConnectionFactory.getConnection()) {
            isRedisConnected = "PONG".equals(connection.ping());
        } catch (Exception e) {
            healthResponse.put("redisError", e.getMessage());
            e.printStackTrace();
        }
        healthResponse.put("redis", isRedisConnected ? "UP" : "DOWN");

        // LeanCloud 状态
        boolean isLeanCloudConnected = false;
        try {
            isLeanCloudConnected = LeanCloudUtils.createObject("ConnectionTest", dataSource, "default");
        } catch (Exception e) {
            healthResponse.put("leancloudError", e.getMessage());
            e.printStackTrace();
        }
        healthResponse.put("leancloud", isLeanCloudConnected ? "UP" : "DOWN");

        // 系统信息
        healthResponse.put("system", getSystemDetails());

        return ResponseEntity.ok(healthResponse);
    }

    private Map<String, Object> checkDataSource(DataSource dataSource, String url, String username) {
        Map<String, Object> dbStatus = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            boolean isDbConnected = connection.isValid(2); // 2秒超时
            dbStatus.put("status", isDbConnected ? "UP" : "DOWN");

            if (isDbConnected) {
                Map<String, Object> dbDetails = new HashMap<>();
                dbDetails.put("url", connection.getMetaData().getURL());
                dbDetails.put("username", connection.getMetaData().getUserName());
                dbDetails.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
                dbDetails.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
                dbStatus.put("details", dbDetails);

                // 添加连接池状态信息
                HikariPoolMXBean poolStats = ((HikariDataSource) dataSource).getHikariPoolMXBean();
                if (poolStats != null) {
                    Map<String, Object> poolDetails = new HashMap<>();
                    poolDetails.put("activeConnections", poolStats.getActiveConnections());
                    poolDetails.put("idleConnections", poolStats.getIdleConnections());
                    poolDetails.put("totalConnections", poolStats.getTotalConnections());
                    poolDetails.put("threadsAwaitingConnection", poolStats.getThreadsAwaitingConnection());
                    dbStatus.put("poolDetails", poolDetails);
                } else {
                    dbStatus.put("poolDetails", "Unable to get pool stats");
                }
            }
        } catch (SQLException e) {
            dbStatus.put("status", "DOWN");
            dbStatus.put("error", e.getMessage());
            e.printStackTrace();
        }
        return dbStatus;
    }

    @GetMapping("/hello")
    @Operation(summary = "Hello endpoint", description = "Returns a simple hello message.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hello message",
                            content = @Content(schema = @Schema(implementation = String.class)))
            })
    public String hello() {
        return "Hello, Phantoms!";
    }


    @GetMapping("/version")
    @Operation(summary = "Version endpoint", description = "Returns the current deployment version of the application, including Git properties.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Version information",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            })
    public ResponseEntity<Map<String, String>> getVersion() {
        Map<String, String> versionResponse = new HashMap<>();
        Properties gitProperties = loadGitProperties();

        // add git information
        for (String key : gitProperties.stringPropertyNames()) {
            versionResponse.put(key, gitProperties.getProperty(key));
        }
        // add app version
        versionResponse.put("app.version", appVersion);

        // add timestamp
        versionResponse.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(versionResponse);
    }
    private Properties loadGitProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("git.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private Map<String, String> getSystemDetails() {
        Map<String, String> details = new HashMap<>();
        details.put("java.version", System.getProperty("java.version"));
        details.put("java.vendor", System.getProperty("java.vendor"));
        details.put("os.name", System.getProperty("os.name"));
        details.put("os.version", System.getProperty("os.version"));
        details.put("os.arch", System.getProperty("os.arch"));
        details.put("user.timezone", System.getProperty("user.timezone"));
        details.put("user.language", System.getProperty("user.language"));
        details.put("user.country", System.getProperty("user.country"));
        return details;
    }

//    @PostMapping("/check/daoyu-key")
//    @Operation(summary = "手动检查DaoYu Key有效性",
//            description = "手动触发DaoYu Key有效性检查，并返回检查结果",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "检查完成",
//                            content = @Content(schema = @Schema(implementation = Map.class)))
//            })
//    public ResponseEntity<Map<String, Object>> checkDaoYuKey() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("timestamp", LocalDateTime.now());
//
//        try {
//            // 获取当前监控状态
//            Map<String, Object> monitorStatus = daoYuKeyMonitorScheduler.getMonitorStatus();
//            response.put("monitorStatus", monitorStatus);
//
//            // 执行手动检查
//            daoYuKeyMonitorScheduler.manualCheck();
//
//            // 获取检查后的状态
//            Map<String, Object> updatedStatus = daoYuKeyMonitorScheduler.getMonitorStatus();
//            response.put("updatedStatus", updatedStatus);
//
//            // 尝试获取登录结果
//            String[] loginResult = risingStonesLoginTool.getDaoYuTokenAndCookie();
//            if (loginResult[0] != null && loginResult[1] != null) {
//                response.put("status", "SUCCESS");
//                response.put("message", "DaoYu Key检查通过，系统正常运行");
//                response.put("tokenAvailable", true);
//                response.put("tokenPrefix", loginResult[0].substring(0, Math.min(20, loginResult[0].length())) + "...");
//                response.put("cookieAvailable", true);
//            } else {
//                response.put("status", "FAILED");
//                response.put("message", "DaoYu Key检查失败，返回的token或cookie为空");
//                response.put("tokenAvailable", false);
//                response.put("cookieAvailable", false);
//            }
//
//        } catch (Exception e) {
//            response.put("status", "ERROR");
//            response.put("message", "DaoYu Key检查过程中发生异常: " + e.getMessage());
//            response.put("error", e.getClass().getSimpleName());
//            response.put("tokenAvailable", false);
//            response.put("cookieAvailable", false);
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/status/daoyu-key")
//    @Operation(summary = "获取DaoYu Key监控状态",
//            description = "获取当前DaoYu Key监控的状态信息，不触发实际检查",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "状态信息获取成功",
//                            content = @Content(schema = @Schema(implementation = Map.class)))
//            })
//    public ResponseEntity<Map<String, Object>> getDaoYuKeyStatus() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("timestamp", LocalDateTime.now());
//
//        try {
//            // 获取监控状态
//            Map<String, Object> monitorStatus = daoYuKeyMonitorScheduler.getMonitorStatus();
//            response.putAll(monitorStatus);
//
//            // 尝试快速检查当前状态（不发送通知）
//            String[] loginResult = risingStonesLoginTool.getDaoYuTokenAndCookie();
//            boolean isKeyValid = loginResult[0] != null && loginResult[1] != null;
//
//            response.put("currentKeyValid", isKeyValid);
//            response.put("lastCheckTime", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//            if (isKeyValid) {
//                response.put("status", "HEALTHY");
//                response.put("message", "DaoYu Key当前有效");
//                response.put("tokenPrefix", loginResult[0].substring(0, Math.min(20, loginResult[0].length())) + "...");
//            } else {
//                response.put("status", "UNHEALTHY");
//                response.put("message", "DaoYu Key当前无效");
//            }
//
//        } catch (Exception e) {
//            response.put("status", "UNKNOWN");
//            response.put("message", "无法确定DaoYu Key状态: " + e.getMessage());
//            response.put("error", e.getClass().getSimpleName());
//            response.put("currentKeyValid", false);
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/refresh/daoyu-key")
//    @Operation(summary = "手动刷新DaoYu Key缓存",
//            description = "手动刷新DaoYu Key缓存并检查有效性",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "刷新完成",
//                            content = @Content(schema = @Schema(implementation = Map.class)))
//            })
//    public ResponseEntity<Map<String, Object>> refreshDaoYuKey() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("timestamp", LocalDateTime.now());
//
//        try {
//            // 获取刷新前的状态
//            Map<String, Object> beforeStatus = daoYuKeyMonitorScheduler.getMonitorStatus();
//            response.put("beforeRefresh", beforeStatus);
//
//            // 执行手动刷新
//            risingStonesLoginTool.refreshDaoYuKeyCache();
//
//            // 等待缓存刷新完成
//            Thread.sleep(3000);
//
//            // 执行检查
//            daoYuKeyMonitorScheduler.manualCheck();
//
//            // 获取刷新后的状态
//            Map<String, Object> afterStatus = daoYuKeyMonitorScheduler.getMonitorStatus();
//            response.put("afterRefresh", afterStatus);
//
//            response.put("status", "SUCCESS");
//            response.put("message", "DaoYu Key缓存刷新完成");
//
//        } catch (Exception e) {
//            response.put("status", "ERROR");
//            response.put("message", "DaoYu Key缓存刷新失败: " + e.getMessage());
//            response.put("error", e.getClass().getSimpleName());
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/config/daoyu-notification")
//    @Operation(summary = "配置DaoYu Key通知设置",
//            description = "修改DaoYu Key监控的通知配置",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "配置更新成功",
//                            content = @Content(schema = @Schema(implementation = Map.class)))
//            })
//    public ResponseEntity<Map<String, Object>> configureDaoYuNotification() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("timestamp", LocalDateTime.now());
//
//        try {
//            // 获取当前配置
//            Map<String, Object> currentStatus = daoYuKeyMonitorScheduler.getMonitorStatus();
//            boolean currentNotifyOnSuccess = (boolean) currentStatus.get("notifyOnSuccess");
//
//            // 切换配置（这里可以根据需求改为接收参数）
//            boolean newNotifyOnSuccess = !currentNotifyOnSuccess;
//            daoYuKeyMonitorScheduler.setNotifyOnSuccess(newNotifyOnSuccess);
//
//            response.put("status", "SUCCESS");
//            response.put("message", "通知配置已更新");
//            response.put("notifyOnSuccess", newNotifyOnSuccess);
//            response.put("previousNotifyOnSuccess", currentNotifyOnSuccess);
//
//        } catch (Exception e) {
//            response.put("status", "ERROR");
//            response.put("message", "配置更新失败: " + e.getMessage());
//            response.put("error", e.getClass().getSimpleName());
//        }
//
//        return ResponseEntity.ok(response);
//    }

}