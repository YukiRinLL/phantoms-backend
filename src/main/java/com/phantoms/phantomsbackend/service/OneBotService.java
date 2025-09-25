package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.dto.ChatRecordDTO;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface OneBotService {

    List<ChatRecord> processOneBotRequest(Map<String, Object> requestBody) throws Exception;

    List<ChatRecordDTO> getLatestMessages(@RequestParam(defaultValue = "30") int limit) throws IOException;

    List<ChatRecord> getLatestTextMessages(@RequestParam(defaultValue = "30") int limit);

    void sendGroupMessageWithDefaultGroup(String message, String groupId) throws Exception;

    void saveUserMessage(String message, Long groupId, Map<String, Object> systemInfo);

    /**
     * 获取本月度消息统计
     * @return 包含三个统计结果的Map
     */
    Map<String, Object> getMonthlyStats();
}