package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.service.LeanCloudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leancloud")
@Tag(name = "LeanCloud", description = "LeanCloud数据存储服务接口")
public class LeanCloudController {

    @Autowired
    private LeanCloudService leanCloudService;

    private static final String DEFAULT_USER_ID = "default";

    @PostMapping("/storeKV")
    @Operation(
            summary = "存储键值对",
            description = "存储一个键值对到LeanCloud",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "存储成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "存储失败")
            }
    )
    public ResponseEntity<String> storeKV(
            @Parameter(description = "键名", required = true) @RequestParam String key,
            @Parameter(description = "键值", required = true) @RequestParam String value) {
        boolean result = leanCloudService.storeKV(key, value, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Storage successful") : ResponseEntity.badRequest().body("Storage failed");
    }

    @GetMapping("/getKV")
    @Operation(
            summary = "获取键值对",
            description = "根据键名从LeanCloud获取对应的值",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "键不存在")
            }
    )
    public ResponseEntity<String> getKV(
            @Parameter(description = "键名", required = true) @RequestParam String key) {
        String value = leanCloudService.getKV(key, DEFAULT_USER_ID);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    @PutMapping("/updateKV")
    @Operation(
            summary = "更新键值对",
            description = "更新LeanCloud中指定键的值",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "更新失败")
            }
    )
    public ResponseEntity<String> updateKV(
            @Parameter(description = "键名", required = true) @RequestParam String key,
            @Parameter(description = "新值", required = true) @RequestParam String newValue) {
        boolean result = leanCloudService.updateKV(key, newValue, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("KV update successful") : ResponseEntity.badRequest().body("KV update failed");
    }

    @DeleteMapping("/deleteKV")
    @Operation(
            summary = "删除键值对",
            description = "从LeanCloud中删除指定的键值对",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "删除失败")
            }
    )
    public ResponseEntity<String> deleteKV(
            @Parameter(description = "键名", required = true) @RequestParam String key) {
        boolean result = leanCloudService.deleteKV(key, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("KV deletion successful") : ResponseEntity.badRequest().body("KV deletion failed");
    }

    @PostMapping("/storeObject")
    @Operation(
            summary = "存储对象",
            description = "存储一个对象到LeanCloud指定的类中",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "存储成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "存储失败")
            }
    )
    public ResponseEntity<String> storeObject(
            @Parameter(description = "类名", required = true) @RequestParam String className,
            @RequestBody Object object) {
        boolean result = leanCloudService.storeObject(className, object, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object storage successful") : ResponseEntity.badRequest().body("Object storage failed");
    }

    @GetMapping("/getObject")
    @Operation(
            summary = "获取对象",
            description = "根据对象ID从LeanCloud获取对象",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "对象不存在")
            }
    )
    public ResponseEntity<Object> getObject(
            @Parameter(description = "类名", required = true) @RequestParam String className,
            @Parameter(description = "对象ID", required = true) @RequestParam String objectId) {
        Map<String, Object> object = leanCloudService.getObject(className, objectId, Map.class, DEFAULT_USER_ID);
        return object != null ? ResponseEntity.ok(object) : ResponseEntity.notFound().build();
    }

    @PutMapping("/updateObject")
    @Operation(
            summary = "更新对象",
            description = "更新LeanCloud中指定对象的属性",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "更新失败")
            }
    )
    public ResponseEntity<String> updateObject(
            @Parameter(description = "类名", required = true) @RequestParam String className,
            @Parameter(description = "对象ID", required = true) @RequestParam String objectId,
            @RequestBody Object object) {
        boolean result = leanCloudService.updateObject(className, objectId, object, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object update successful") : ResponseEntity.badRequest().body("Object update failed");
    }

    @DeleteMapping("/deleteObject")
    @Operation(
            summary = "删除对象",
            description = "从LeanCloud中删除指定对象",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "删除失败")
            }
    )
    public ResponseEntity<String> deleteObject(
            @Parameter(description = "类名", required = true) @RequestParam String className,
            @Parameter(description = "对象ID", required = true) @RequestParam String objectId) {
        boolean result = leanCloudService.deleteObject(className, objectId, DEFAULT_USER_ID);
        return result ? ResponseEntity.ok("Object deletion successful") : ResponseEntity.badRequest().body("Object deletion failed");
    }

    @GetMapping("/queryObject")
    @Operation(
            summary = "查询对象",
            description = "根据条件查询LeanCloud中的对象",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "未找到匹配对象")
            }
    )
    public ResponseEntity<List<?>> queryObject(
            @Parameter(description = "类名", required = true) @RequestParam String className,
            @Parameter(description = "查询字段", required = true) @RequestParam String key,
            @Parameter(description = "查询值", required = true) @RequestParam Object value) {
        List<?> results = leanCloudService.queryObject(className, key, value, Map.class, DEFAULT_USER_ID);
        return !results.isEmpty() ? ResponseEntity.ok(results) : ResponseEntity.notFound().build();
    }
}
