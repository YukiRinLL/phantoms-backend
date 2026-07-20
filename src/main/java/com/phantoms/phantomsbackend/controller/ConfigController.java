package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> getConfig(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        String value = systemConfigService.getString(key);
        if (value != null) {
            result.put("key", key);
            result.put("value", value);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{key}")
    public ResponseEntity<Map<String, Object>> setConfig(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {
        String value = body.get("value");
        String description = body.get("description");
        
        if (value == null || value.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "value is required"));
        }
        
        systemConfigService.setConfig(key, value, description);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("description", description);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{key}/json")
    public ResponseEntity<Map<String, Object>> setJsonConfig(
            @PathVariable String key,
            @RequestBody Map<String, Object> body) {
        Object value = body.get("value");
        String description = body.getOrDefault("description", "").toString();
        
        if (value == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "value is required"));
        }
        
        systemConfigService.setJsonConfig(key, value, description);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("description", description);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{key}/json")
    public ResponseEntity<Object> getJsonConfig(@PathVariable String key) {
        String value = systemConfigService.getString(key);
        if (value != null && !value.isEmpty()) {
            try {
                return ResponseEntity.ok(JSON.parse(value));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON"));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String key) {
        systemConfigService.deleteConfig(key);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/housing/targets")
    public ResponseEntity<Map<String, Object>> setHousingTargets(@RequestBody List<Map<String, String>> targets) {
        systemConfigService.setJsonConfig("housing.sale.notify.targets", targets, "房屋销售监控目标配置列表");
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", "housing.sale.notify.targets");
        result.put("value", targets);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/housing/targets")
    public ResponseEntity<List<Map<String, String>>> getHousingTargets() {
        List<Map<String, String>> targets = systemConfigService.getJson(
                "housing.sale.notify.targets",
                new TypeReference<List<Map<String, String>>>() {},
                List.of()
        );
        return ResponseEntity.ok(targets);
    }

    @GetMapping("/ff14/news")
    public ResponseEntity<Map<String, Object>> getFF14NewsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("group-ids", systemConfigService.getString("ff14.news.group-ids", ""));
        config.put("category-codes", systemConfigService.getString("ff14.news.category-codes", ""));
        return ResponseEntity.ok(config);
    }

    @PostMapping("/ff14/news")
    public ResponseEntity<Map<String, Object>> setFF14NewsConfig(@RequestBody Map<String, String> body) {
        if (body.containsKey("group-ids")) {
            systemConfigService.setConfig("ff14.news.group-ids", body.get("group-ids"), "国服新闻发送到的群ID，多个用逗号分隔");
        }
        if (body.containsKey("category-codes")) {
            systemConfigService.setConfig("ff14.news.category-codes", body.get("category-codes"), "国服新闻分类码，多个用逗号分隔");
        }
        return getFF14NewsConfig();
    }

    @GetMapping("/ff14/global-news")
    public ResponseEntity<Map<String, Object>> getFF14GlobalNewsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("group-ids", systemConfigService.getString("ff14.global-news.group-ids", ""));
        config.put("enable-news", systemConfigService.getBoolean("ff14.global-news.enable-news", true));
        config.put("enable-topics", systemConfigService.getBoolean("ff14.global-news.enable-topics", true));
        return ResponseEntity.ok(config);
    }

    @PostMapping("/ff14/global-news")
    public ResponseEntity<Map<String, Object>> setFF14GlobalNewsConfig(@RequestBody Map<String, Object> body) {
        if (body.containsKey("group-ids")) {
            systemConfigService.setConfig("ff14.global-news.group-ids", body.get("group-ids").toString(), "国际服新闻发送到的群ID，多个用逗号分隔");
        }
        if (body.containsKey("enable-news")) {
            systemConfigService.setConfig("ff14.global-news.enable-news", body.get("enable-news").toString(), "是否启用新闻RSS");
        }
        if (body.containsKey("enable-topics")) {
            systemConfigService.setConfig("ff14.global-news.enable-topics", body.get("enable-topics").toString(), "是否启用活动RSS");
        }
        return getFF14GlobalNewsConfig();
    }

    @GetMapping("/ff14/crystal-news")
    public ResponseEntity<Map<String, Object>> getFF14CrystalNewsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("group-ids", systemConfigService.getString("ff14.crystal-news.group-ids", ""));
        return ResponseEntity.ok(config);
    }

    @PostMapping("/ff14/crystal-news")
    public ResponseEntity<Map<String, Object>> setFF14CrystalNewsConfig(@RequestBody Map<String, String> body) {
        if (body.containsKey("group-ids")) {
            systemConfigService.setConfig("ff14.crystal-news.group-ids", body.get("group-ids"), "水晶世界新闻发送到的群ID，多个用逗号分隔");
        }
        return getFF14CrystalNewsConfig();
    }

    @GetMapping("/napcat/groups")
    public ResponseEntity<Map<String, Object>> getNapcatGroupsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("default-group-id", systemConfigService.getString("napcat.default-group-id", ""));
        config.put("phantom-group-id", systemConfigService.getString("napcat.phantom-group-id", ""));
        config.put("crystal-group-id", systemConfigService.getString("napcat.crystal-group-id", ""));
        config.put("admin-qq", systemConfigService.getString("napcat.admin-qq", ""));
        return ResponseEntity.ok(config);
    }

    @PostMapping("/napcat/groups")
    public ResponseEntity<Map<String, Object>> setNapcatGroupsConfig(@RequestBody Map<String, String> body) {
        if (body.containsKey("default-group-id")) {
            systemConfigService.setConfig("napcat.default-group-id", body.get("default-group-id"), "默认群ID");
        }
        if (body.containsKey("phantom-group-id")) {
            systemConfigService.setConfig("napcat.phantom-group-id", body.get("phantom-group-id"), "Phantom群ID");
        }
        if (body.containsKey("crystal-group-id")) {
            systemConfigService.setConfig("napcat.crystal-group-id", body.get("crystal-group-id"), "Crystal群ID");
        }
        if (body.containsKey("admin-qq")) {
            systemConfigService.setConfig("napcat.admin-qq", body.get("admin-qq"), "管理员QQ");
        }
        return getNapcatGroupsConfig();
    }

    @GetMapping("/monitor")
    public ResponseEntity<Map<String, Object>> getMonitorConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("check.enabled", systemConfigService.getBoolean("monitor.check.enabled", true));
        config.put("notification.email", systemConfigService.getString("monitor.notification.email", ""));
        config.put("notification.notify-on-success", systemConfigService.getBoolean("monitor.notification.notify-on-success", false));
        return ResponseEntity.ok(config);
    }

    @PostMapping("/monitor")
    public ResponseEntity<Map<String, Object>> setMonitorConfig(@RequestBody Map<String, Object> body) {
        if (body.containsKey("check.enabled")) {
            systemConfigService.setConfig("monitor.check.enabled", body.get("check.enabled").toString(), "是否启用监控");
        }
        if (body.containsKey("notification.email")) {
            systemConfigService.setConfig("monitor.notification.email", body.get("notification.email").toString(), "通知邮箱");
        }
        if (body.containsKey("notification.notify-on-success")) {
            systemConfigService.setConfig("monitor.notification.notify-on-success", body.get("notification.notify-on-success").toString(), "成功时是否通知");
        }
        return getMonitorConfig();
    }
}
