package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.phantoms.phantomsbackend.pojo.dto.ChatRecordDTO;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import com.phantoms.phantomsbackend.service.OneBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "OneBot Controller", description = "OneBot/QQ机器人消息处理接口")
public class OneBotController {

    private static final Logger logger = LoggerFactory.getLogger(OneBotController.class);

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
    @Operation(
            summary = "处理OneBot请求",
            description = "接收并处理来自OneBot的消息请求",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "请求处理成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求处理失败")
            }
    )
    public ResponseEntity<String> handleOneBotRequest(@RequestBody Map<String, Object> requestBody) {
        String postType = (String) requestBody.get("post_type");
        String noticeType = (String) requestBody.get("notice_type");
        String subType = (String) requestBody.get("sub_type");

        logger.info("Received request - post_type: {}, notice_type: {}, sub_type: {}", postType, noticeType, subType);
        logger.debug("Full request: {}", requestBody);

        try {
            List<ChatRecord> chatRecords = oneBotService.processOneBotRequest(requestBody);

            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("status", "ok");

            ArrayNode data = objectMapper.createArrayNode();
            for (ChatRecord chatRecord : chatRecords) {
                ObjectNode recordJson = objectMapper.createObjectNode();
                recordJson.put("id", chatRecord.getId().toString());
                recordJson.put("message_type", chatRecord.getMessageType());
                recordJson.put("message", chatRecord.getMessage());
                data.add(recordJson);
            }
            jsonResponse.set("data", data);

            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            e.printStackTrace();

            ObjectNode jsonError = objectMapper.createObjectNode();
            jsonError.put("status", "failed");
            jsonError.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonError.toString());
        }
    }

    @GetMapping("/onebot/latest")
    @Operation(
            summary = "获取最新消息",
            description = "获取最新的聊天消息记录",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    public ResponseEntity<List<ChatRecordDTO>> getLatestMessages(
            @Parameter(description = "返回消息数量限制，默认30条") @RequestParam(defaultValue = "30") int limit) {
        try {
            List<ChatRecordDTO> latestMessages = oneBotService.getLatestMessages(limit);
            return ResponseEntity.ok(latestMessages);
        } catch (Exception e) {
            logger.error("Error fetching latest messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/onebot/latest/text")
    @Operation(
            summary = "获取最新文本消息",
            description = "获取最新的纯文本聊天消息记录",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    public ResponseEntity<List<ChatRecord>> getLatestTextMessages(
            @Parameter(description = "返回消息数量限制，默认30条") @RequestParam(defaultValue = "30") int limit) {
        try {
            List<ChatRecord> latestMessages = oneBotService.getLatestTextMessages(limit);
            return ResponseEntity.ok(latestMessages);
        } catch (Exception e) {
            logger.error("Error fetching latest messages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/onebot/send-to-group")
    @Operation(
            summary = "发送消息到群组",
            description = "向指定QQ群发送消息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "消息发送成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "消息发送失败")
            }
    )
    public ResponseEntity<String> sendToGroup(
            @RequestBody Map<String, Object> requestBody,
            @Parameter(description = "群组ID，不指定则使用默认群组") @RequestParam(required = false) String groupId) {
        try {
            if (StringUtils.isEmpty(groupId)) {
                groupId = defaultGroupId;
            }

            String message = (String) requestBody.get("message");
            Map<String, Object> systemInfo = (Map<String, Object>) requestBody.get("systemInfo");

            logger.info("System Information: {}", systemInfo);

            if (systemInfo == null) {
                systemInfo = new HashMap<>();
            }

            Map<String, Object> network = (Map<String, Object>) systemInfo.getOrDefault("network", new HashMap<>());
            Map<String, Object> connection = (Map<String, Object>) network.getOrDefault("connection", new HashMap<>());

            oneBotService.saveUserMessage(message, Long.valueOf(groupId.toString()), systemInfo);
            oneBotService.sendGroupMessage(message, groupId);

            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            logger.error("Error sending message to group: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"status\":\"failed\",\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/onebot/monthly-stats")
    @Operation(
            summary = "获取月度统计",
            description = "获取指定月份的消息统计数据",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @Parameter(description = "年份，不指定则使用当前年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份，不指定则使用当前月份") @RequestParam(required = false) Integer month) {
        try {
            LocalDate now = LocalDate.now();
            int targetYear = (year != null) ? year : now.getYear();
            int targetMonth = (month != null) ? month : now.getMonthValue();

            Map<String, Object> monthlyStats = oneBotService.getMonthlyStats(targetYear, targetMonth);

            return ResponseEntity.ok(monthlyStats);
        } catch (Exception e) {
            logger.error("Error fetching monthly stats: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failed");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/onebot/user-stats")
    @Operation(
            summary = "获取用户消息统计",
            description = "获取指定用户的消息统计数据",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    public ResponseEntity<Map<String, Object>> getUserMessageStats(
            @Parameter(description = "搜索关键词（用户名或QQ号）", required = true) @RequestParam String search,
            @Parameter(description = "统计天数，默认30天") @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> userStats = oneBotService.getUserMessageStats(search, days);
            return ResponseEntity.ok(userStats);
        } catch (Exception e) {
            logger.error("Error fetching user message stats: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "failed");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
