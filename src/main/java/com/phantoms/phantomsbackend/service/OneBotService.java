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

    void sendGroupMessage(String message, String groupId) throws Exception;

    void saveUserMessage(String message, Long groupId, Map<String, Object> systemInfo);

    /**
     * 获取指定年月度的消息统计
     * @param year 年份
     * @param month 月份 (1-12)
     * @return 包含多个统计结果的Map
     */
    Map<String, Object> getMonthlyStats(int year, int month);

    /**
     * 获取指定年月度的消息总数
     */
    long getMonthlyMessageCount(int year, int month);

    /**
     * 获取指定年月度的图片总数
     */
    long getMonthlyImageCount(int year, int month);

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