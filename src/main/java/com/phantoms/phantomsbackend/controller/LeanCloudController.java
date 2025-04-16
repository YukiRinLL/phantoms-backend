package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.service.LeanCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leancloud")
public class LeanCloudController {

    @Autowired
    private LeanCloudService leanCloudService;

    // 使用默认用户ID
    private static final String DEFAULT_USER_ID = "default";

    @PostMapping("/storeKV")
    public ResponseEntity<String> storeKV(@RequestParam String key, @RequestParam String value) {
        boolean result = leanCloudService.storeKV(key, value, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Storage successful") : ResponseEntity.badRequest().body("Storage failed");
    }

    @GetMapping("/getKV")
    public ResponseEntity<String> getKV(@RequestParam String key) {
        String value = leanCloudService.getKV(key, DEFAULT_USER_ID);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @PutMapping("/updateKV")
    public ResponseEntity<String> updateKV(@RequestParam String key, @RequestParam String newValue) {
        boolean result = leanCloudService.updateKV(key, newValue, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("KV update successful") : ResponseEntity.badRequest().body("KV update failed");
    }

    @DeleteMapping("/deleteKV")
    public ResponseEntity<String> deleteKV(@RequestParam String key) {
        boolean result = leanCloudService.deleteKV(key, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("KV deletion successful") : ResponseEntity.badRequest().body("KV deletion failed");
    }

    @PostMapping("/createObject")
    public ResponseEntity<String> createObject(@RequestParam String className, @RequestParam String key, @RequestParam String value) {
        boolean result = leanCloudService.createObject(className, key, value, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object creation successful") : ResponseEntity.badRequest().body("Object creation failed");
    }

    @GetMapping("/queryObject")
    public ResponseEntity<List<?>> queryObject(@RequestParam String className, @RequestParam String key, @RequestParam String value) {
        List<?> results = leanCloudService.queryObject(className, key, value, DEFAULT_USER_ID);
        return !results.isEmpty() ? ResponseEntity.ok(results) : ResponseEntity.notFound().build();
    }

    @PutMapping("/updateObject")
    public ResponseEntity<String> updateObject(@RequestParam String className, @RequestParam String objectId, @RequestParam String key, @RequestParam String newValue) {
        boolean result = leanCloudService.updateObject(className, objectId, key, newValue, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object update successful") : ResponseEntity.badRequest().body("Object update failed");
    }

    @DeleteMapping("/deleteObject")
    public ResponseEntity<String> deleteObject(@RequestParam String className, @RequestParam String objectId) {
        boolean result = leanCloudService.deleteObject(className, objectId, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object deletion successful") : ResponseEntity.badRequest().body("Object deletion failed");
    }
}