package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import com.phantoms.phantomsbackend.repository.onebot.ChatRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String handleOneBotRequest(@RequestBody Map<String, Object> requestBody) {
        // 打印接收到的请求内容
        System.out.println("Received request: " + requestBody);

        // 获取消息类型和消息内容
        String messageType = (String) requestBody.get("message_type");
        Long userId = requestBody.get("user_id") != null ? Long.valueOf(requestBody.get("user_id").toString()) : null;
        Long groupId = requestBody.get("group_id") != null ? Long.valueOf(requestBody.get("group_id").toString()) : null;
        String message = (String) requestBody.get("message");

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
        return "Message received and saved";
    }
}