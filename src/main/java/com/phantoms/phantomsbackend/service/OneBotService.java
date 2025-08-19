package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface OneBotService {

    List<ChatRecord> processOneBotRequest(Map<String, Object> requestBody) throws Exception;

    List<ChatRecord> getLatestMessages(@RequestParam(defaultValue = "30") int limit);

    List<ChatRecord> getLatestTextMessages(@RequestParam(defaultValue = "30") int limit);

    void sendGroupMessageWithDefaultGroup(String message, String groupId) throws Exception;

    void saveUserMessage(String message, Long groupId, Map<String, Object> systemInfo);
}