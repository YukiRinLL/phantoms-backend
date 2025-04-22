package com.phantoms.phantomsbackend.controller;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
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
    private JdbcTemplate jdbcTemplate;

    @Value("${app.version:unknown}")
    private String appVersion;

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
    @Operation(summary = "Health Check endpoint", description = "Returns the health status of the application, including database and LeanCloud status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Health status",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("timestamp", LocalDateTime.now());

        try (Connection connection = dataSource.getConnection()) {
            boolean isDbConnected = connection.isValid(2); // 2秒超时
            healthResponse.put("database", isDbConnected ? "UP" : "DOWN");

            if (isDbConnected) {
                Map<String, Object> dbDetails = new HashMap<>();
                dbDetails.put("url", connection.getMetaData().getURL());
                dbDetails.put("username", connection.getMetaData().getUserName());
                dbDetails.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
                dbDetails.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
                healthResponse.put("databaseDetails", dbDetails);

                // 添加连接池状态信息
                HikariPoolMXBean poolStats = ((HikariDataSource) dataSource).getHikariPoolMXBean();
                if (poolStats != null) {
                    Map<String, Object> poolDetails = new HashMap<>();
                    poolDetails.put("activeConnections", poolStats.getActiveConnections());
                    poolDetails.put("idleConnections", poolStats.getIdleConnections());
                    poolDetails.put("totalConnections", poolStats.getTotalConnections());
                    poolDetails.put("threadsAwaitingConnection", poolStats.getThreadsAwaitingConnection());
                    healthResponse.put("connectionPoolDetails", poolDetails);
                } else {
                    healthResponse.put("connectionPoolDetails", "Unable to get pool stats");
                }
            }
        } catch (SQLException e) {
            healthResponse.put("database", "DOWN");
            healthResponse.put("databaseError", e.getMessage());
            e.printStackTrace();
        }

        // 检查 LeanCloud 状态
        boolean isLeanCloudConnected = false;
        try {
            isLeanCloudConnected = LeanCloudUtils.createObject("ConnectionTest", dataSource, "default");
        } catch (Exception e) {
            healthResponse.put("leancloudError", e.getMessage());
            e.printStackTrace();
        }
        healthResponse.put("leancloud", isLeanCloudConnected ? "UP" : "DOWN");

        healthResponse.put("system", getSystemDetails());

        return ResponseEntity.ok(healthResponse);
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
}