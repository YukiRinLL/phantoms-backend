package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.primary.SystemConfig;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public ResponseEntity<List<SystemConfig>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigService.getAllConfigEntities());
    }

    @GetMapping("/{key}")
    public ResponseEntity<SystemConfig> getConfig(@PathVariable String key) {
        SystemConfig config = systemConfigService.getConfigEntity(key);
        if (config != null) {
            return ResponseEntity.ok(config);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<SystemConfig> createConfig(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String value = body.get("value");
        String description = body.get("description");

        if (key == null || key.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (value == null) {
            value = "";
        }

        systemConfigService.setConfig(key, value, description);

        SystemConfig config = new SystemConfig();
        config.setKey(key);
        config.setValue(value);
        config.setDescription(description != null ? description : "");
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{key}")
    public ResponseEntity<SystemConfig> updateConfig(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {
        String value = body.get("value");
        String description = body.get("description");

        if (value == null) {
            return ResponseEntity.badRequest().build();
        }

        systemConfigService.setConfig(key, value, description);

        SystemConfig config = new SystemConfig();
        config.setKey(key);
        config.setValue(value);
        config.setDescription(description != null ? description : "");
        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String key) {
        systemConfigService.deleteConfig(key);
        return ResponseEntity.ok().build();
    }
}
