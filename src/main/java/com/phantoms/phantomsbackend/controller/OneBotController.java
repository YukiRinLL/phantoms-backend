package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phantoms.phantomsbackend.pojo.dto.ChatRecordDTO;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import com.phantoms.phantomsbackend.service.OneBotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OneBotController {

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    @Autowired
    private final OneBotService oneBotService;
    private final ObjectMapper objectMapper;

    public OneBotController(OneBotService oneBotService, ObjectMapper objectMapper) {
        this.oneBotService = oneBotService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/onebot")
    public ResponseEntity<String> handleOneBotRequest(@RequestBody Map<String, Object> requestBody) {
        // 打印接收到的请求内容
        System.out.println("Received request: " + requestBody);

        try {
            // 调用服务层处理请求
            List<ChatRecord> chatRecords = oneBotService.processOneBotRequest(requestBody);

            // 使用 Jackson 构建 JSON 响应
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("status", "ok");

            ArrayNode data = objectMapper.createArrayNode();
            for (ChatRecord chatRecord : chatRecords) {
                ObjectNode recordJson = objectMapper.createObjectNode();
                recordJson.put("id", chatRecord.getId().toString());
                data.add(recordJson);
            }
            jsonResponse.set("data", data);

            // 返回 JSON 格式的响应
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error processing request: " + e.getMessage());

            // 使用 Jackson 构建 JSON 错误响应
            ObjectNode jsonError = objectMapper.createObjectNode();
            jsonError.put("status", "failed");
            jsonError.put("error", e.getMessage());

            // 返回 JSON 格式的错误响应
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonError.toString());
        }
    }

    @GetMapping("/onebot/latest")
    public ResponseEntity<List<ChatRecordDTO>> getLatestMessages(@RequestParam(defaultValue = "30") int limit) {
        try {
            // 调用服务层获取最新的几条消息
            List<ChatRecordDTO> latestMessages = oneBotService.getLatestMessages(limit);

            // 返回 JSON 格式的响应
            return ResponseEntity.ok(latestMessages);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error fetching latest messages: " + e.getMessage());
            // 返回错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/onebot/latest/text")
    public ResponseEntity<List<ChatRecord>> getLatestTextMessages(@RequestParam(defaultValue = "30") int limit) {
        try {
            // 调用服务层获取最新的几条消息
            List<ChatRecord> latestMessages = oneBotService.getLatestTextMessages(limit);

            // 返回 JSON 格式的响应
            return ResponseEntity.ok(latestMessages);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error fetching latest messages: " + e.getMessage());
            // 返回错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/onebot/send-to-group")
    public ResponseEntity<String> sendToGroup(@RequestBody Map<String, Object> requestBody, @RequestParam(required = false) String groupId) {
        try {
            if (StringUtils.isEmpty(groupId)) {
                groupId = defaultGroupId;
            }

            // Extract message and system information
            String message = (String) requestBody.get("message");
            Map<String, Object> systemInfo = (Map<String, Object>) requestBody.get("systemInfo");

            // Log system information for investigation purposes
            System.out.println("System Information: " + systemInfo);

            // Ensure systemInfo is not null
            if (systemInfo == null) {
                systemInfo = new HashMap<>();
            }

            // Ensure network and connection are not null
            Map<String, Object> network = (Map<String, Object>) systemInfo.getOrDefault("network", new HashMap<>());
            Map<String, Object> connection = (Map<String, Object>) network.getOrDefault("connection", new HashMap<>());

            // Save the message and system information to the database
            oneBotService.saveUserMessage(message, Long.valueOf(groupId.toString()), systemInfo);

            // Send the message to the default group
            oneBotService.sendGroupMessageWithDefaultGroup(message, groupId);

            // Return success response
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            // Log error
            System.err.println("Error sending message to group: " + e.getMessage());

            // Return error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"status\":\"failed\",\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}