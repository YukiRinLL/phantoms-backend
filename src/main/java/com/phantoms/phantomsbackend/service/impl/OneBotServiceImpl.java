package com.phantoms.phantomsbackend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.common.utils.RedisUtil;
import com.phantoms.phantomsbackend.pojo.dto.ChatRecordDTO;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.UserMessage;
import com.phantoms.phantomsbackend.repository.primary.onebot.PrimaryChatRecordRepository;
import com.phantoms.phantomsbackend.repository.primary.onebot.UserMessageRepository;
import com.phantoms.phantomsbackend.service.OneBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
public class OneBotServiceImpl implements OneBotService {

    @Autowired
    private PrimaryChatRecordRepository chatRecordRepository;

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    private String phantomGroupId = "787909466";

    // Redis缓存相关配置
    private static final String GROUP_MEMBER_CACHE_PREFIX = "group:members:";
    private static final long GROUP_MEMBER_CACHE_EXPIRE_HOURS = 24; // 缓存24小时

    @Override
    public List<ChatRecord> processOneBotRequest(Map<String, Object> requestBody) throws Exception {
        String postType = (String) requestBody.get("post_type");

        // 处理通知事件（如戳一戳）
        if ("notice".equals(postType)) {
            return handleNoticeEvent(requestBody);
        }
        // 处理消息事件
        else if ("message".equals(postType)) {
            return handleMessageEvent(requestBody);
        }
        // 其他类型的事件
        else {
            return handleOtherEvent(requestBody);
        }
    }

    /**
     * 处理通知事件（戳一戳等）
     */
    private List<ChatRecord> handleNoticeEvent(Map<String, Object> requestBody) {
        String noticeType = (String) requestBody.get("notice_type");
        List<ChatRecord> chatRecords = new ArrayList<>();

        if ("notify".equals(noticeType)) {
            String subType = (String) requestBody.get("sub_type");

            // 处理戳一戳事件
            if ("poke".equals(subType)) {
                Long userId = requestBody.get("user_id") != null ? Long.valueOf(requestBody.get("user_id").toString()) : null;
                Long targetId = requestBody.get("target_id") != null ? Long.valueOf(requestBody.get("target_id").toString()) : null;
                Long groupId = requestBody.get("group_id") != null ? Long.valueOf(requestBody.get("group_id").toString()) : null;

                // 创建戳一戳记录
                ChatRecord chatRecord = new ChatRecord();
                chatRecord.setMessageType("notice_poke");
                chatRecord.setUserId(userId);
                chatRecord.setGroupId(groupId);
                chatRecord.setMessage(String.format("用户 %s 戳了戳 %s", userId, targetId));
                chatRecord.setTimestamp(LocalDateTime.now());
                chatRecord.setCreatedAt(LocalDateTime.now());
                chatRecord.setUpdatedAt(LocalDateTime.now());

                chatRecordRepository.save(chatRecord);
                chatRecords.add(chatRecord);

                // 可以在这里添加自动回复逻辑
                handlePokeReply(userId, targetId, groupId);
            }
        }

        return chatRecords;
    }

    /**
     * 处理消息事件（原来的逻辑）
     */
    private List<ChatRecord> handleMessageEvent(Map<String, Object> requestBody) throws Exception {
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
                ChatRecord chatRecord = createChatRecord(messageType, userId, groupId, messageElement.toString());
                chatRecordRepository.save(chatRecord);
                chatRecords.add(chatRecord);
            }
        } else if (messageObj instanceof String) {
            // 如果是String，直接保存为一条ChatRecord
            ChatRecord chatRecord = createChatRecord(messageType, userId, groupId, (String) messageObj);
            chatRecordRepository.save(chatRecord);
            chatRecords.add(chatRecord);
        } else {
            throw new IllegalArgumentException("message must be a string or a list");
        }

        return chatRecords;
    }

    /**
     * 处理其他类型事件
     */
    private List<ChatRecord> handleOtherEvent(Map<String, Object> requestBody) {
        // 记录其他类型的事件，但不做特殊处理
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setMessageType("other");
        chatRecord.setUserId(requestBody.get("user_id") != null ? Long.valueOf(requestBody.get("user_id").toString()) : null);
        chatRecord.setGroupId(requestBody.get("group_id") != null ? Long.valueOf(requestBody.get("group_id").toString()) : null);
        chatRecord.setMessage("其他事件: " + requestBody.get("post_type"));
        chatRecord.setTimestamp(LocalDateTime.now());
        chatRecord.setCreatedAt(LocalDateTime.now());
        chatRecord.setUpdatedAt(LocalDateTime.now());

        chatRecordRepository.save(chatRecord);
        return List.of(chatRecord);
    }

    /**
     * 创建聊天记录对象（辅助方法）
     */
    private ChatRecord createChatRecord(String messageType, Long userId, Long groupId, String message) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setMessageType(messageType);
        chatRecord.setUserId(userId);
        chatRecord.setGroupId(groupId);
        chatRecord.setMessage(message);
        chatRecord.setTimestamp(LocalDateTime.now());
        chatRecord.setCreatedAt(LocalDateTime.now());
        chatRecord.setUpdatedAt(LocalDateTime.now());
        return chatRecord;
    }

    /**
     * 处理戳一戳的自动回复
     */
    private void handlePokeReply(Long userId, Long targetId, Long groupId) {
        try {
            // 如果被戳的是机器人自己
            if (targetId != null && isBotUserId(targetId)) {
                String[] replies = {
                    "别戳我嘛~",
                    "戳我干嘛呀~",
                    "再戳我要生气了！",
                    "戳回去~"
                };
                String reply = replies[(int) (Math.random() * replies.length)];

                if (groupId != null) {
                    napCatQQUtil.sendGroupMessage(groupId.toString(), reply);
                }
            }
            // 如果是机器人戳别人（理论上不会发生，但可以记录）
            else if (userId != null && isBotUserId(userId)) {
                // 机器人主动戳别人，记录日志
                System.out.println("机器人戳了用户: " + targetId);
            }
        } catch (Exception e) {
            System.err.println("处理戳一戳回复失败: " + e.getMessage());
        }
    }

    /**
     * 判断是否是机器人自己的用户ID
     */
    private boolean isBotUserId(Long userId) {
        // 这里需要根据你的机器人实际用户ID来判断
        // 假设机器人的用户ID是 3146672611（从日志中看到的self_id）
        return userId != null && userId == 3146672611L;
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
            napCatQQUtil.setGroupBan(groupId.toString(), userId.toString(), 5 * 60);
        }
    }

    private void handleImageMessage(Long userId, Long groupId, String imageUrl) throws Exception {
        // 检查发送者一天内发送了多少图片
        if (checkDailyImageCount(userId, groupId)) {
            napCatQQUtil.setGroupBan(groupId.toString(), userId.toString(), 5 * 60);
        }

        // 检查用户连续发送图片的数量
        else if (checkRecentMessagesAreAllImages(userId, groupId, 7)) {
            napCatQQUtil.setGroupBan(groupId.toString(), userId.toString(), 10 * 60);
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
    public List<ChatRecordDTO> getLatestMessages(int limit) throws IOException {
        List<ChatRecord> chatRecords = chatRecordRepository.findTopByOrderByCreatedAtDesc(limit);
        List<ChatRecordDTO> chatRecordDTOs = new ArrayList<>();

        // 统计所有需要查询的群组ID
        Set<Long> groupIds = chatRecords.stream()
            .map(ChatRecord::getGroupId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // 缓存每个群组的成员列表
        Map<Long, Map<Long, String>> groupMemberMap = new HashMap<>();

        for (Long groupId : groupIds) {
            Map<Long, String> memberMap = getGroupMemberMap(groupId);
            groupMemberMap.put(groupId, memberMap);
        }

        for (ChatRecord chatRecord : chatRecords) {
            ChatRecordDTO chatRecordDTO = new ChatRecordDTO();
            chatRecordDTO.setId(chatRecord.getId());
            chatRecordDTO.setMessageType(chatRecord.getMessageType());
            chatRecordDTO.setUserId(chatRecord.getUserId());
            chatRecordDTO.setGroupId(chatRecord.getGroupId());
            chatRecordDTO.setMessage(chatRecord.getMessage());
            chatRecordDTO.setTimestamp(chatRecord.getTimestamp());
            chatRecordDTO.setCreatedAt(chatRecord.getCreatedAt());
            chatRecordDTO.setUpdatedAt(chatRecord.getUpdatedAt());

            // 设置昵称
            Map<Long, String> memberMap = groupMemberMap.get(chatRecord.getGroupId());
            if (memberMap != null) {
                chatRecordDTO.setNickname(memberMap.getOrDefault(chatRecord.getUserId(), "Unknown" + chatRecord.getUserId()));
            } else {
                chatRecordDTO.setNickname("Unknown" + chatRecord.getUserId());
            }

            chatRecordDTOs.add(chatRecordDTO);
        }

        return chatRecordDTOs;
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

        // Ensure systemInfo is not null
        if (systemInfo == null) {
            systemInfo = new HashMap<>();
        }

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

    @Override
    public Map<String, Object> getMonthlyStats() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 添加总量统计数据
            result.put("monthlyMessageCount", getMonthlyMessageCount());
            result.put("monthlyImageCount", getMonthlyImageCount());
            result.put("totalMessageCount", getTotalMessageCount());
            result.put("totalImageCount", getTotalImageCount());

            // 1. 发送消息数量排名
            List<Object[]> messageRanking = chatRecordRepository.findMonthlyMessageRanking();
            List<Map<String, Object>> messageRankingList = new ArrayList<>();

            // 2. 发送图片数量排名
            List<Object[]> imageRanking = chatRecordRepository.findMonthlyImageRanking();
            List<Map<String, Object>> imageRankingList = new ArrayList<>();

            // 3. 图片比例排名
            List<Object[]> ratioRanking = chatRecordRepository.findMonthlyImageRatioRanking();
            List<Map<String, Object>> ratioRankingList = new ArrayList<>();

            // 收集所有需要查询的群组ID（假设统计的是默认群组的数据）
            Set<Long> groupIds = new HashSet<>();
            groupIds.add(Long.parseLong(phantomGroupId));

            // 缓存每个群组的成员列表
            Map<Long, Map<Long, String>> groupMemberMap = new HashMap<>();

            for (Long groupId : groupIds) {
                Map<Long, String> memberMap = getGroupMemberMap(groupId);
                groupMemberMap.put(groupId, memberMap);
            }

            // 获取默认群组的成员映射
            Map<Long, String> defaultGroupMemberMap = groupMemberMap.get(Long.parseLong(phantomGroupId));
            if (defaultGroupMemberMap == null) {
                defaultGroupMemberMap = new HashMap<>();
            }

            // 处理消息排名
            for (Object[] row : messageRanking) {
                Long userId = (Long) row[0];
                Long messageCount = (Long) row[1];

                Map<String, Object> userStat = new HashMap<>();
                userStat.put("userId", userId);
                userStat.put("messageCount", messageCount);
                userStat.put("nickname", defaultGroupMemberMap.getOrDefault(userId, "Unknown" + userId));
                messageRankingList.add(userStat);
            }
            result.put("messageRanking", messageRankingList);

            // 处理图片排名
            for (Object[] row : imageRanking) {
                Long userId = (Long) row[0];
                Long imageCount = (Long) row[1];

                Map<String, Object> userStat = new HashMap<>();
                userStat.put("userId", userId);
                userStat.put("imageCount", imageCount);
                userStat.put("nickname", defaultGroupMemberMap.getOrDefault(userId, "Unknown" + userId));
                imageRankingList.add(userStat);
            }
            result.put("imageRanking", imageRankingList);

            // 处理图片比例排名
            for (Object[] row : ratioRanking) {
                Long userId = (Long) row[0];
                Long totalMessages = (Long) row[1];
                Long totalImages = ((Number) row[2]).longValue();

                Double imageRatio;
                if (row[3] instanceof BigDecimal) {
                    imageRatio = ((BigDecimal) row[3]).doubleValue();
                } else if (row[3] instanceof Double) {
                    imageRatio = (Double) row[3];
                } else if (row[3] instanceof Number) {
                    imageRatio = ((Number) row[3]).doubleValue();
                } else {
                    imageRatio = 0.0; // 默认值
                }

                Map<String, Object> userStat = new HashMap<>();
                userStat.put("userId", userId);
                userStat.put("totalMessages", totalMessages);
                userStat.put("totalImages", totalImages);
                userStat.put("imageRatio", String.format("%.2f%%", imageRatio * 100));
                userStat.put("nickname", defaultGroupMemberMap.getOrDefault(userId, "Unknown" + userId));
                ratioRankingList.add(userStat);
            }
            result.put("ratioRanking", ratioRankingList);

        } catch (Exception e) {
            System.err.println("Error generating monthly stats: " + e.getMessage());
            throw new RuntimeException("生成月度统计失败", e);
        }

        return result;
    }

    // ========== 公共缓存方法 ==========

    /**
     * 获取群组成员映射（优先从缓存获取）
     */
    private Map<Long, String> getGroupMemberMap(Long groupId) {
        if (groupId == null) {
            return new HashMap<>();
        }

        try {
            String cacheKey = GROUP_MEMBER_CACHE_PREFIX + groupId;

            // 先从Redis缓存中尝试获取群组成员信息
            Map<Long, String> memberMap = getGroupMemberMapFromCache(cacheKey);

            if (memberMap != null && !memberMap.isEmpty()) {
                return memberMap;
            }

            // 缓存中没有，从NapCat服务器查询群组成员
            memberMap = fetchGroupMemberMapFromNapCat(groupId);

            if (memberMap != null && !memberMap.isEmpty()) {
                // 将查询结果存入缓存
                saveGroupMemberMapToCache(cacheKey, memberMap);
                return memberMap;
            }

            // 如果查询失败，返回空映射
            return new HashMap<>();

        } catch (Exception e) {
            System.err.println("Error getting group member map for group " + groupId + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 从Redis缓存中获取群组成员映射
     */
    @SuppressWarnings("unchecked")
    private Map<Long, String> getGroupMemberMapFromCache(String cacheKey) {
        try {
            Object cachedData = redisUtil.get(cacheKey);
            if (cachedData instanceof Map) {
                return (Map<Long, String>) cachedData;
            }
        } catch (Exception e) {
            System.err.println("Error getting group member map from cache for key " + cacheKey + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * 将数据保存到Redis缓存
     */
    private void saveToCache(String cacheKey, Object data, long expireTime, TimeUnit timeUnit) {
        try {
            redisUtil.setWithExpire(cacheKey, data, expireTime, timeUnit);
        } catch (Exception e) {
            System.err.println("Error saving data to cache for key " + cacheKey + ": " + e.getMessage());
        }
    }

    /**
     * 将群组成员映射保存到Redis缓存
     */
    private void saveGroupMemberMapToCache(String cacheKey, Map<Long, String> memberMap) {
        saveToCache(cacheKey, memberMap, GROUP_MEMBER_CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 从NapCat服务器获取群组成员映射
     */
    private Map<Long, String> fetchGroupMemberMapFromNapCat(Long groupId) {
        try {
            String groupMemberListJson = napCatQQUtil.getGroupMemberList(groupId.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode groupMemberListNode;

            try {
                groupMemberListNode = objectMapper.readTree(groupMemberListJson);
            } catch (Exception e) {
                System.err.println("Error parsing group member list JSON for group " + groupId + ": " + e.getMessage());
                return new HashMap<>();
            }

            Map<Long, String> memberMap = new HashMap<>();
            JsonNode dataNode = groupMemberListNode.path("data");

            for (JsonNode memberNode : dataNode) {
                Long userId = memberNode.path("user_id").asLong();
                // 优先使用群名片，如果没有则使用昵称
                String card = memberNode.path("card").asText();
                String nickname = memberNode.path("nickname").asText();
                String displayName = card.isEmpty() ? nickname : card;

                if (!displayName.isEmpty()) {
                    memberMap.put(userId, displayName);
                }
            }

            return memberMap;
        } catch (Exception e) {
            System.err.println("Error fetching group member map from NapCat for group " + groupId + ": " + e.getMessage());
            return new HashMap<>();
        }
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

    @Override
    public long getMonthlyMessageCount() {
        return chatRecordRepository.countMonthlyMessages();
    }

    @Override
    public long getMonthlyImageCount() {
        return chatRecordRepository.countMonthlyImages();
    }

    @Override
    public long getTotalMessageCount() {
        return chatRecordRepository.count();
    }

    @Override
    public long getTotalImageCount() {
        return chatRecordRepository.countByMessageContaining("[CQ:image");
    }
}