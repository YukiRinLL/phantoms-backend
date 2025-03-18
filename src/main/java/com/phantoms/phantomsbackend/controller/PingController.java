package com.phantoms.phantomsbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Ping successful");
        response.put("details", getSystemDetails());

        return ResponseEntity.ok(response);
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