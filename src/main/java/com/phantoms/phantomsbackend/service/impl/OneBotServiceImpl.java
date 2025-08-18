package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.UserMessage;
import com.phantoms.phantomsbackend.repository.primary.onebot.PrimaryChatRecordRepository;
import com.phantoms.phantomsbackend.repository.primary.onebot.UserMessageRepository;
import com.phantoms.phantomsbackend.service.OneBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OneBotServiceImpl implements OneBotService {

    @Autowired
    private PrimaryChatRecordRepository chatRecordRepository;

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    @Override
    public ChatRecord processOneBotRequest(Map<String, Object> requestBody) throws Exception {
        // 获取消息类型和消息内容
        String messageType = (String) requestBody.get("message_type");
        Long userId = requestBody.get("user_id") != null ? Long.valueOf(requestBody.get("user_id").toString()) : null;
        Long groupId = requestBody.get("group_id") != null ? Long.valueOf(requestBody.get("group_id").toString()) : null;

        // 检查message字段是否为List
        Object messageObj = requestBody.get("message");
        String message;
        if (messageObj instanceof List) {
            // 如果是List，尝试将List中的第一个元素作为消息内容
            List<?> messageList = (List<?>) messageObj;
            if (!messageList.isEmpty()) {
                message = messageList.get(0).toString();
            } else {
                message = "";
            }
        } else if (messageObj instanceof String) {
            message = (String) messageObj;
        } else {
            throw new IllegalArgumentException("message must be a string or a list");
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

        return chatRecord;
    }

    @Override
    public List<ChatRecord> getLatestMessages(int limit) {
        // 查询最新的几条消息，只返回 type=text 的消息
        return chatRecordRepository.findTopByOrderByCreatedAtDescWithText(limit);
    }

    @Override
    public void sendGroupMessageWithDefaultGroup(String message, String groupId) throws Exception {
        String targetGroupId = groupId != null ? groupId : defaultGroupId;
        napCatQQUtil.sendGroupMessage(targetGroupId, message);
    }

    @Override
    public void saveUserMessage(String message, Long groupId, Map<String, Object> systemInfo) {
        UserMessage userMessage = new UserMessage();
        userMessage.setMessage(message);
        userMessage.setGroupId(groupId);

        // Extract nested browser information
        Map<String, Object> browser = (Map<String, Object>) systemInfo.getOrDefault("browser", new HashMap<>());
        userMessage.setUserAgent((String) browser.getOrDefault("userAgent", ""));
        userMessage.setAppName((String) browser.getOrDefault("appName", ""));
        userMessage.setAppVersion((String) browser.getOrDefault("appVersion", ""));
        userMessage.setPlatform((String) browser.getOrDefault("platform", ""));
        userMessage.setLanguage((String) browser.getOrDefault("language", ""));
        userMessage.setCookiesEnabled((Boolean) browser.getOrDefault("cookiesEnabled", false));
        userMessage.setDoNotTrack((String) browser.getOrDefault("doNotTrack", ""));
        userMessage.setJavaEnabled((Boolean) browser.getOrDefault("javaEnabled", false));
        userMessage.setOnLine((Boolean) browser.getOrDefault("onLine", false));

        // Extract nested device information
        Map<String, Object> device = (Map<String, Object>) systemInfo.getOrDefault("device", new HashMap<>());
        userMessage.setScreenWidth((Integer) device.getOrDefault("screenWidth", 0));
        userMessage.setScreenHeight((Integer) device.getOrDefault("screenHeight", 0));
        userMessage.setColorDepth((Integer) device.getOrDefault("colorDepth", 0));
        userMessage.setPixelRatio((Double) device.getOrDefault("pixelRatio", 0.0));
        userMessage.setOrientation((String) device.getOrDefault("orientation", ""));
        userMessage.setAvailableMemory((Integer) device.getOrDefault("availableMemory", 0));
        userMessage.setHardwareConcurrency((Integer) device.getOrDefault("hardwareConcurrency", 0));

        // Extract nested network information
        Map<String, Object> network = (Map<String, Object>) systemInfo.getOrDefault("network", new HashMap<>());
        Map<String, Object> connection = (Map<String, Object>) network.getOrDefault("connection", new HashMap<>());
        userMessage.setConnectionType((String) connection.getOrDefault("effectiveType", ""));
        userMessage.setDownlink((Double) connection.getOrDefault("downlink", 0.0));
        userMessage.setEffectiveType((String) connection.getOrDefault("effectiveType", ""));

        // Fix the conversion of rtt from Integer to double
        Object rttValue = connection.getOrDefault("rtt", 0);
        if (rttValue instanceof Integer) {
            userMessage.setRtt(((Integer) rttValue).doubleValue());
        } else {
            userMessage.setRtt(0.0);
        }

        // Extract nested battery information
        Map<String, Object> battery = (Map<String, Object>) systemInfo.getOrDefault("battery", new HashMap<>());
        Map<String, Object> status = (Map<String, Object>) battery.getOrDefault("status", new HashMap<>());
        userMessage.setBatteryCharging((Boolean) status.getOrDefault("charging", false));
        userMessage.setBatteryLevel((Double) status.getOrDefault("level", 0.0));
        userMessage.setBatteryChargingTime((Double) status.getOrDefault("chargingTime", 0.0));
        userMessage.setBatteryDischargingTime((Double) status.getOrDefault("dischargingTime", 0.0));

        // Extract timezone
        userMessage.setTimeZone((String) systemInfo.getOrDefault("timezone", ""));

        // Extract plugins and mimeTypes
        userMessage.setPlugins(((List<?>) systemInfo.getOrDefault("plugins", new ArrayList<>())).toArray(new String[0]));
        userMessage.setMimeTypes(((List<?>) systemInfo.getOrDefault("mimeTypes", new ArrayList<>())).toArray(new String[0]));

        // Extract geolocation
        Map<String, Object> geolocation = (Map<String, Object>) systemInfo.getOrDefault("geolocation", new HashMap<>());
        userMessage.setLatitude((Double) geolocation.getOrDefault("latitude", 0.0));
        userMessage.setLongitude((Double) geolocation.getOrDefault("longitude", 0.0));

        // Fix the conversion of accuracy from Integer to double
        Object accuracyValue = geolocation.getOrDefault("accuracy", 0.0);
        if (accuracyValue instanceof Integer) {
            userMessage.setAccuracy(((Integer) accuracyValue).doubleValue());
        } else if (accuracyValue instanceof Double) {
            userMessage.setAccuracy((Double) accuracyValue);
        } else {
            userMessage.setAccuracy(0.0);
        }

        userMessage.setAltitude((Double) geolocation.getOrDefault("altitude", 0.0));
        userMessage.setAltitudeAccuracy((Double) geolocation.getOrDefault("altitudeAccuracy", 0.0));
        userMessage.setHeading((Double) geolocation.getOrDefault("heading", 0.0));
        userMessage.setSpeed((Double) geolocation.getOrDefault("speed", 0.0));

        userMessage.setTimestamp(LocalDateTime.now());

        userMessageRepository.save(userMessage);
    }
}