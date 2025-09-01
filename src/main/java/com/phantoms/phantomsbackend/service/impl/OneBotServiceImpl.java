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
    public List<ChatRecord> processOneBotRequest(Map<String, Object> requestBody) throws Exception {
        // 获取消息类型和消息内容
        String messageType = (String) requestBody.get("message_type");
        Long userId = requestBody.get("user_id") != null ? Long.valueOf(requestBody.get("user_id").toString()) : null;
        Long groupId = requestBody.get("group_id") != null ? Long.valueOf(requestBody.get("group_id").toString()) : null;

        // 检查message字段是否为List
        Object messageObj = requestBody.get("message");
        List<ChatRecord> chatRecords = new ArrayList<>();
        if (messageObj instanceof List) {
            // 如果是List，将List中的每个元素分别保存为一条ChatRecord
            List<?> messageList = (List<?>) messageObj;
            for (Object messageElement : messageList) {
                ChatRecord chatRecord = new ChatRecord();
                chatRecord.setMessageType(messageType);
                chatRecord.setUserId(userId);
                chatRecord.setGroupId(groupId);
                chatRecord.setMessage(messageElement.toString());
                chatRecord.setTimestamp(LocalDateTime.now());
                chatRecord.setCreatedAt(LocalDateTime.now());
                chatRecord.setUpdatedAt(LocalDateTime.now());
                chatRecordRepository.save(chatRecord);
                chatRecords.add(chatRecord);

//                handleMessage(userId, groupId, (String) messageObj);
//                // 检查图片消息
//                if (isImageMessage(messageElement)) {
//                    handleImageMessage(userId, groupId, messageElement.toString());
//                }
            }
        } else if (messageObj instanceof String) {
            // 如果是String，直接保存为一条ChatRecord
            ChatRecord chatRecord = new ChatRecord();
            chatRecord.setMessageType(messageType);
            chatRecord.setUserId(userId);
            chatRecord.setGroupId(groupId);
            chatRecord.setMessage((String) messageObj);
            chatRecord.setTimestamp(LocalDateTime.now());
            chatRecord.setCreatedAt(LocalDateTime.now());
            chatRecord.setUpdatedAt(LocalDateTime.now());
            chatRecordRepository.save(chatRecord);
            chatRecords.add(chatRecord);

//            handleMessage(userId, groupId, (String) messageObj);
//            // 检查图片消息
//            if (isImageMessage(messageObj)) {
//                handleImageMessage(userId, groupId, (String) messageObj);
//            }
        } else {
            throw new IllegalArgumentException("message must be a string or a list");
        }

        return chatRecords;
    }

    private boolean isImageMessage(Object message) {
        if (message instanceof Map) {
            Map<?, ?> messageMap = (Map<?, ?>) message;
            return "image".equals(messageMap.get("type"));
        } else if (message instanceof String) {
            return ((String) message).contains("[CQ:image");
        }
        return false;
    }

    private void handleMessage(Long userId, Long groupId, String imageUrl) throws Exception {
        // 检查group内是否连续发送了三个相同内容的消息
        if (checkConsecutive(groupId)) {
            napCatQQUtil.muteGroupMember(groupId.toString(), userId.toString(), 5 * 60);
        }
    }

    private void handleImageMessage(Long userId, Long groupId, String imageUrl) throws Exception {
        // 检查发送者一天内发送了多少图片
        if (checkDailyImageCount(userId, groupId)) {
            napCatQQUtil.muteGroupMember(groupId.toString(), userId.toString(), 5 * 60);
        }

        // 检查用户连续发送图片的数量
        else if (checkRecentMessagesAreAllImages(userId, groupId, 7)) {
            napCatQQUtil.muteGroupMember(groupId.toString(), userId.toString(), 10 * 60);
        }
    }

    private boolean checkConsecutive(Long groupId) {
        List<ChatRecord> recentRecords = chatRecordRepository.findTop3ByGroupIdOrderByCreatedAtDesc(groupId);
        if (recentRecords.size() < 3) {
            return false;
        }
        String lastMessage = recentRecords.get(0).getMessage();
        return recentRecords.stream().allMatch(record -> record.getMessage().equals(lastMessage));
    }

    private boolean checkDailyImageCount(Long userId, Long groupId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        long imageCount = chatRecordRepository.countByUserIdAndGroupIdAndMessageTypeAndTimestampAfter(userId, groupId, oneDayAgo);
        long totalMessageCount = chatRecordRepository.countByUserIdAndGroupIdAndTimestampAfter(userId, groupId, oneDayAgo);
        return totalMessageCount > 20 && imageCount * 2 >= totalMessageCount;
    }

    private boolean checkRecentMessagesAreAllImages(Long userId, Long groupId, int messageCount) {
        List<ChatRecord> recentRecords = chatRecordRepository.findTopByUserIdAndGroupIdOrderByCreatedAtDesc(userId, groupId, messageCount);
        if (recentRecords.size() < messageCount) {
            return false;
        }
        return recentRecords.stream().allMatch(record -> record.getMessage().contains("type=image"));
    }

    @Override
    public List<ChatRecord> getLatestMessages(int limit) {
        return chatRecordRepository.findTopByOrderByCreatedAtDesc(limit);
    }

    @Override
    public List<ChatRecord> getLatestTextMessages(int limit) {
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
        userMessage.setPixelRatio(getDoubleValue(device, "pixelRatio", 0.0));
        userMessage.setOrientation((String) device.getOrDefault("orientation", ""));
        userMessage.setAvailableMemory((Integer) device.getOrDefault("availableMemory", 0));
        userMessage.setHardwareConcurrency((Integer) device.getOrDefault("hardwareConcurrency", 0));

        // Extract nested network information
        Map<String, Object> network = (Map<String, Object>) systemInfo.getOrDefault("network", new HashMap<>());
        Map<String, Object> connection = (Map<String, Object>) network.getOrDefault("connection", new HashMap<>());
        userMessage.setConnectionType((String) connection.getOrDefault("effectiveType", ""));
        userMessage.setDownlink(getDoubleValue(connection, "downlink", 0.0));
        userMessage.setEffectiveType((String) connection.getOrDefault("effectiveType", ""));
        userMessage.setRtt(getDoubleValue(connection, "rtt", 0.0));

        // Extract nested battery information
        Map<String, Object> battery = (Map<String, Object>) systemInfo.getOrDefault("battery", new HashMap<>());
        Map<String, Object> status = (Map<String, Object>) battery.getOrDefault("status", new HashMap<>());
        userMessage.setBatteryCharging((Boolean) status.getOrDefault("charging", false));
        userMessage.setBatteryLevel(getDoubleValue(status, "level", 0.0));
        userMessage.setBatteryChargingTime(getDoubleValue(status, "chargingTime", 0.0));
        userMessage.setBatteryDischargingTime(getDoubleValue(status, "dischargingTime", 0.0));

        // Extract timezone
        userMessage.setTimeZone((String) systemInfo.getOrDefault("timezone", ""));

        // Extract plugins and mimeTypes
        userMessage.setPlugins(((List<?>) systemInfo.getOrDefault("plugins", new ArrayList<>())).toArray(new String[0]));
        userMessage.setMimeTypes(((List<?>) systemInfo.getOrDefault("mimeTypes", new ArrayList<>())).toArray(new String[0]));

        // Extract geolocation
        Map<String, Object> geolocation = (Map<String, Object>) systemInfo.getOrDefault("geolocation", new HashMap<>());
        userMessage.setLatitude(getDoubleValue(geolocation, "latitude", 0.0));
        userMessage.setLongitude(getDoubleValue(geolocation, "longitude", 0.0));
        userMessage.setAccuracy(getDoubleValue(geolocation, "accuracy", 0.0));
        userMessage.setAltitude(getDoubleValue(geolocation, "altitude", 0.0));
        userMessage.setAltitudeAccuracy(getDoubleValue(geolocation, "altitudeAccuracy", 0.0));
        userMessage.setHeading(getDoubleValue(geolocation, "heading", 0.0));
        userMessage.setSpeed(getDoubleValue(geolocation, "speed", 0.0));

        userMessage.setTimestamp(LocalDateTime.now());

        userMessageRepository.save(userMessage);
    }

    // 通用方法：从Map中获取并转换为double
    private double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        Object value = map.getOrDefault(key, defaultValue);
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            return defaultValue;
        }
    }
}