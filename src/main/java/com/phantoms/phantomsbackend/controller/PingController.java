package com.phantoms.phantomsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PingController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Ping successful");
        response.put("details", getSystemDetails());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
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
            }
        } catch (SQLException e) {
            healthResponse.put("database", "DOWN");
            healthResponse.put("databaseError", e.getMessage());
            e.printStackTrace();
        }

        healthResponse.put("system", getSystemDetails());
        healthResponse.put("status", "UP");

        return ResponseEntity.ok(healthResponse);
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

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Phantoms!";
    }
}