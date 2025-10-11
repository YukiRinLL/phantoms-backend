package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.dto.ChatRecordDTO;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;
import java.util.List;

public interface OneBotService {

    List<ChatRecord> processOneBotRequest(Map<String, Object> requestBody) throws Exception;

    List<ChatRecordDTO> getLatestMessages(@RequestParam(defaultValue = "30") int limit) throws IOException;

    List<ChatRecord> getLatestTextMessages(@RequestParam(defaultValue = "30") int limit);

    void sendGroupMessageWithDefaultGroup(String message, String groupId) throws Exception;

    void saveUserMessage(String message, Long groupId, Map<String, Object> systemInfo);

    /**
     * 获取本月度消息统计
     * @return 包含多个统计结果的Map
     */
    Map<String, Object> getMonthlyStats();

    /**
     * 获取本月消息总数
     */
    long getMonthlyMessageCount();

    /**
     * 获取本月图片总数
     */
    long getMonthlyImageCount();

    /**
     * 获取群消息总数（所有时间）
     */
    long getTotalMessageCount();

    /**
     * 获取群图片总数（所有时间）
     */
    long getTotalImageCount();

    Map<String, Object> getUserMessageStats(String search, int days);
}