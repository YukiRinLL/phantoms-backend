package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;

import java.util.Map;

public interface OneBotService {
    ChatRecord processOneBotRequest(Map<String, Object> requestBody) throws Exception;
}