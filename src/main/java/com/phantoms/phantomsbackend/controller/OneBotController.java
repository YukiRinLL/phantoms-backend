package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import com.phantoms.phantomsbackend.repository.onebot.ChatRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class OneBotController {

    @Autowired
    private ChatRecordRepository chatRecordRepository;

    @PostMapping("/onebot")
    public ResponseEntity<String> handleOneBotRequest(@RequestBody Map<String, Object> requestBody) {
        // 打印接收到的请求内容
        System.out.println("Received request: " + requestBody);

        try {
            // 获取消息类型和消息内容
            String messageType = (String) requestBody.get("message_type");
            Long userId = requestBody.get("qq_user_id") != null ? Long.valueOf(requestBody.get("qq_user_id").toString()) : null;
            Long groupId = requestBody.get("qq_group_id") != null ? Long.valueOf(requestBody.get("qq_group_id").toString()) : null;
            String message = (String) requestBody.get("message");

            // 类型检查和错误处理
            if (!(messageType instanceof String)) {
                throw new IllegalArgumentException("message_type must be a string");
            }
            if (!(message instanceof String)) {
                throw new IllegalArgumentException("message must be a string");
            }

            // 创建ChatRecord对象并保存到数据库
            ChatRecord chatRecord = new ChatRecord();
            chatRecord.setMessageType(messageType);
            chatRecord.setUserId(userId);
            chatRecord.setGroupId(groupId);
            chatRecord.setMessage(message);
            chatRecord.setTimestamp(LocalDateTime.now());
            chatRecord.setCreatedAt(LocalDateTime.now());
            chatRecord.setUpdatedAt(LocalDateTime.now());
            chatRecordRepository.save(chatRecord);

            // 返回响应
            return ResponseEntity.ok("Message received and saved");
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("Error processing request: " + e.getMessage());
            // 返回错误响应
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing request: " + e.getMessage());
        }
    }
}