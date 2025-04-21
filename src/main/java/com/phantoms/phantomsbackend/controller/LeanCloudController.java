package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.service.LeanCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leancloud")
public class LeanCloudController {

    @Autowired
    private LeanCloudService leanCloudService;

    // 使用默认用户ID
    private static final String DEFAULT_USER_ID = "default";

    // 存储键值对
    @PostMapping("/storeKV")
    public ResponseEntity<String> storeKV(@RequestParam String key, @RequestParam String value) {
        boolean result = leanCloudService.storeKV(key, value, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Storage successful") : ResponseEntity.badRequest().body("Storage failed");
    }

    // 获取键值对
    @GetMapping("/getKV")
    public ResponseEntity<String> getKV(@RequestParam String key) {
        String value = leanCloudService.getKV(key, DEFAULT_USER_ID);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    // 更新键值对
    @PutMapping("/updateKV")
    public ResponseEntity<String> updateKV(@RequestParam String key, @RequestParam String newValue) {
        boolean result = leanCloudService.updateKV(key, newValue, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("KV update successful") : ResponseEntity.badRequest().body("KV update failed");
    }

    // 删除键值对
    @DeleteMapping("/deleteKV")
    public ResponseEntity<String> deleteKV(@RequestParam String key) {
        boolean result = leanCloudService.deleteKV(key, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("KV deletion successful") : ResponseEntity.badRequest().body("KV deletion failed");
    }

    // 存储对象
    @PostMapping("/storeObject")
    public ResponseEntity<String> storeObject(@RequestParam String className, @RequestBody Object object) {
        boolean result = leanCloudService.storeObject(className, object, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object storage successful") : ResponseEntity.badRequest().body("Object storage failed");
    }

    // 获取对象
    @GetMapping("/getObject")
    public ResponseEntity<Object> getObject(@RequestParam String className, @RequestParam String objectId) {
        Map<String, Object> object = leanCloudService.getObject(className, objectId, Map.class, DEFAULT_USER_ID);
        return object != null ? ResponseEntity.ok(object) : ResponseEntity.notFound().build();
    }

    // 更新对象
    @PutMapping("/updateObject")
    public ResponseEntity<String> updateObject(@RequestParam String className, @RequestParam String objectId, @RequestBody Object object) {
        boolean result = leanCloudService.updateObject(className, objectId, object, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object update successful") : ResponseEntity.badRequest().body("Object update failed");
    }

    // 删除对象
    @DeleteMapping("/deleteObject")
    public ResponseEntity<String> deleteObject(@RequestParam String className, @RequestParam String objectId) {
        boolean result = leanCloudService.deleteObject(className, objectId, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object deletion successful") : ResponseEntity.badRequest().body("Object deletion failed");
    }

    // 查询对象
    @GetMapping("/queryObject")
    public ResponseEntity<List<?>> queryObject(@RequestParam String className, @RequestParam String key, @RequestParam Object value) {
        List<?> results = leanCloudService.queryObject(className, key, value, Map.class, DEFAULT_USER_ID);
        return !results.isEmpty() ? ResponseEntity.ok(results) : ResponseEntity.notFound().build();
    }
}