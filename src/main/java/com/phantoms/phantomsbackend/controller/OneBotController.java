package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import com.phantoms.phantomsbackend.service.OneBotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OneBotController {

    @Autowired
    private OneBotService oneBotService;

    @PostMapping("/onebot")
    public ResponseEntity<String> handleOneBotRequest(@RequestBody Map<String, Object> requestBody) {
        // 打印接收到的请求内容
        System.out.println("Received request: " + requestBody);

        try {
            // 调用服务层处理请求
            ChatRecord chatRecord = oneBotService.processOneBotRequest(requestBody);

            // 使用 JSONObject 构建 JSON 响应
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "ok");
            JSONObject data = new JSONObject();
            data.put("id", chatRecord.getId());
            jsonResponse.put("data", data);

            // 返回 JSON 格式的响应
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error processing request: " + e.getMessage());

            // 使用 JSONObject 构建 JSON 错误响应
            JSONObject jsonError = new JSONObject();
            jsonError.put("status", "failed");
            jsonError.put("error", e.getMessage());

            // 返回 JSON 格式的错误响应
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonError.toString());
        }
    }

    @GetMapping("/onebot/latest")
    public ResponseEntity<List<ChatRecord>> getLatestMessages(@RequestParam(defaultValue = "30") int limit) {
        try {
            // 调用服务层获取最新的几条消息
            List<ChatRecord> latestMessages = oneBotService.getLatestMessages(limit);

            // 返回 JSON 格式的响应
            return ResponseEntity.ok(latestMessages);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error fetching latest messages: " + e.getMessage());
            // 返回错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}