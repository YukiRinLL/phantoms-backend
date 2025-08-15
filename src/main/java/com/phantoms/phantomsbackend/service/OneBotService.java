package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface OneBotService {
    ChatRecord processOneBotRequest(Map<String, Object> requestBody) throws Exception;

    List<ChatRecord> getLatestMessages(@RequestParam(defaultValue = "30") int limit);

    void sendGroupMessageWithDefaultGroup(String message, @RequestParam(required = false) String groupId) throws Exception;
}