package com.phantoms.phantomsbackend.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NapCatQQUtil {

    @Value("${napcat.http-server-url}")
    private String httpServerUrl;

    @Value("${napcat.access-token}")
    private String accessToken;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 通用请求方法
     */
    private String makeRequest(String endpoint, Map<String, Object> params) throws IOException {
        String url = httpServerUrl + endpoint + "?access_token=" + accessToken;
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            objectMapper.writeValueAsString(params)
        );

        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // ==================== 基础消息发送方法 ====================

    /**
     * 发送私聊消息
     */
    public String sendPrivateMessage(String userId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message", message);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送群聊消息
     */
    public String sendGroupMessage(String groupId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message", message);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送消息（自动判断类型）
     */
    public String sendMessage(String messageType, String targetId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_type", messageType);
        params.put(messageType.equals("private") ? "user_id" : "group_id", targetId);
        params.put("message", message);
        return makeRequest("/send_msg", params);
    }

    // ==================== 账号相关方法 ====================

    /**
     * 设置账号信息
     */
    public String setAccountProfile(String nickname, String company, String email, String college,
        String personalNote) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("nickname", nickname);
        params.put("company", company);
        params.put("email", email);
        params.put("college", college);
        params.put("personal_note", personalNote);
        return makeRequest("/set_profile", params);
    }

    /**
     * 获取被过滤好友请求
     */
    public String getFilteredFriendRequests() throws IOException {
        return makeRequest("/get_filtered_friend_requests", new HashMap<>());
    }

    /**
     * 获取推荐好友/群聊卡片
     */
    public String getRecommendCards() throws IOException {
        return makeRequest("/get_recommend_cards", new HashMap<>());
    }

    /**
     * 处理被过滤好友请求
     */
    public String handleFilteredFriendRequest(String requestId, boolean approve, String remark) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("request_id", requestId);
        params.put("approve", approve);
        params.put("remark", remark);
        return makeRequest("/handle_filtered_friend_request", params);
    }

    /**
     * 获取当前账号在线客户端列表
     */
    public String getOnlineClients() throws IOException {
        return makeRequest("/get_online_clients", new HashMap<>());
    }

    /**
     * 设置消息已读
     */
    public String markMessageAsRead(String groupId, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (groupId != null) {
            params.put("group_id", groupId);
        }
        if (userId != null) {
            params.put("user_id", userId);
        }
        return makeRequest("/mark_msg_as_read", params);
    }

    /**
     * 获取推荐群聊卡片
     */
    public String getRecommendGroupCards() throws IOException {
        return makeRequest("/get_recommend_group_cards", new HashMap<>());
    }

    /**
     * 设置在线状态
     * 状态列表: online-在线, away-离开, busy-忙碌, invisible-隐身
     */
    public String setOnlineStatus(String status) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        return makeRequest("/set_online_status", params);
    }

    /**
     * 获取好友分组列表
     */
    public String getFriendCategories() throws IOException {
        return makeRequest("/get_friend_categories", new HashMap<>());
    }

    /**
     * 设置头像
     */
    public String setAvatar(String imagePath) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("image", imagePath);
        return makeRequest("/set_avatar", params);
    }

    /**
     * 点赞
     */
    public String sendLike(String userId, Integer times) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("times", times);
        return makeRequest("/send_like", params);
    }

    /**
     * 设置私聊已读
     */
    public String markPrivateAsRead(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/mark_private_as_read", params);
    }

    /**
     * 设置群聊已读
     */
    public String markGroupAsRead(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/mark_group_as_read", params);
    }

    /**
     * 创建收藏
     */
    public String createFavorite(String content) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        return makeRequest("/create_favorite", params);
    }

    /**
     * 处理好友请求
     */
    public String handleFriendRequest(String flag, boolean approve, String remark) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("flag", flag);
        params.put("approve", approve);
        params.put("remark", remark);
        return makeRequest("/handle_friend_request", params);
    }

    /**
     * 设置个性签名
     */
    public String setSignature(String signature) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("signature", signature);
        return makeRequest("/set_signature", params);
    }

    /**
     * 获取登录号信息
     */
    public String getLoginInfo() throws IOException {
        return makeRequest("/get_login_info", new HashMap<>());
    }

    /**
     * 最近消息列表 - 获取每个会话最新的消息
     */
    public String getRecentMessages() throws IOException {
        return makeRequest("/get_recent_msg", new HashMap<>());
    }

    /**
     * 获取账号信息
     */
    public String getAccountInfo() throws IOException {
        return makeRequest("/get_account_info", new HashMap<>());
    }

    /**
     * 获取好友列表
     */
    public String getFriendList() throws IOException {
        return makeRequest("/get_friend_list", new HashMap<>());
    }

    /**
     * 设置所有消息已读
     */
    public String markAllAsRead() throws IOException {
        return makeRequest("/mark_all_as_read", new HashMap<>());
    }

    /**
     * 获取点赞列表
     */
    public String getLikeList() throws IOException {
        return makeRequest("/get_like_list", new HashMap<>());
    }

    /**
     * 获取收藏表情
     */
    public String getFavoriteEmoticons() throws IOException {
        return makeRequest("/get_favorite_emoticons", new HashMap<>());
    }

    /**
     * 删除好友
     */
    public String deleteFriend(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/delete_friend", params);
    }

    /**
     * 获取在线机型
     */
    public String getOnlineModel() throws IOException {
        return makeRequest("/_get_model_show", new HashMap<>());
    }

    /**
     * 设置在线机型
     */
    public String setOnlineModel(String model) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("model", model);
        return makeRequest("/_set_model_show", params);
    }

    /**
     * 获取用户状态
     */
    public String getUserStatus(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/get_user_status", params);
    }

    /**
     * 获取状态
     */
    public String getStatus() throws IOException {
        return makeRequest("/get_status", new HashMap<>());
    }

    /**
     * 获取小程序卡片
     */
    public String getMiniAppCard() throws IOException {
        return makeRequest("/get_miniapp_card", new HashMap<>());
    }

    /**
     * 获取单向好友列表
     */
    public String getUnidirectionalFriendList() throws IOException {
        return makeRequest("/get_unidirectional_friend_list", new HashMap<>());
    }

    /**
     * 设置自定义在线状态
     */
    public String setCustomStatus(String status) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        return makeRequest("/set_custom_status", params);
    }

    /**
     * 设置好友备注
     */
    public String setFriendRemark(String userId, String remark) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("remark", remark);
        return makeRequest("/set_friend_remark", params);
    }

    // ==================== 群聊消息相关方法 ====================

    /**
     * 发送群文本消息
     */
    public String sendGroupText(String groupId, String text) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message", text);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群艾特消息
     */
    public String sendGroupAt(String groupId, String userId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("message", message);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群图片
     */
    public String sendGroupImage(String groupId, String imageUrl) throws IOException {
        // 创建图片消息对象
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("file", imageUrl);
        imageData.put("summary", "[图片]");
        
        Map<String, Object> imageMessage = new HashMap<>();
        imageMessage.put("type", "image");
        imageMessage.put("data", imageData);
        
        // 创建消息列表
        List<Map<String, Object>> messageList = new ArrayList<>();
        messageList.add(imageMessage);
        
        // 设置请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message", messageList);
        
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群系统表情
     */
    public String sendGroupFace(String groupId, String faceId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("face_id", faceId);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群JSON消息
     */
    public String sendGroupJson(String groupId, String json) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("json", json);
        return makeRequest("/send_group_json", params);
    }

    /**
     * 发送群语音
     */
    public String sendGroupVoice(String groupId, String voiceUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("voice", voiceUrl);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群视频
     */
    public String sendGroupVideo(String groupId, String videoUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("video", videoUrl);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群回复消息
     */
    public String sendGroupReply(String groupId, String messageId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message_id", messageId);
        params.put("message", message);
        return makeRequest("/send_group_msg", params);
    }

    /**
     * 发送群聊音乐卡片
     */
    public String sendGroupMusic(String groupId, String musicId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("music_id", musicId);
        return makeRequest("/send_group_music", params);
    }

    /**
     * 发送群聊骰子
     */
    public String sendGroupDice(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/send_group_dice", params);
    }

    /**
     * 发送群聊猜拳
     */
    public String sendGroupRPS(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/send_group_rps", params);
    }

    /**
     * 发送群合并转发消息
     */
    public String sendGroupForward(String groupId, String messages) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("messages", messages);
        return makeRequest("/send_group_forward_msg", params);
    }

    /**
     * 发送群文件
     */
    public String sendGroupFile(String groupId, String fileUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file", fileUrl);
        return makeRequest("/send_group_file", params);
    }

    /**
     * 消息转发到群
     */
    public String forwardToGroup(String groupId, String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message_id", messageId);
        return makeRequest("/forward_to_group", params);
    }

    /**
     * 发送群聊戳一戳
     */
    public String sendGroupPoke(String groupId, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        return makeRequest("/send_group_poke", params);
    }

    /**
     * 发送群聊自定义音乐卡片
     */
    public String sendGroupCustomMusic(String groupId, Map<String, Object> musicData) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.putAll(musicData);
        return makeRequest("/send_group_custom_music", params);
    }

    // ==================== 私聊消息相关方法 ====================

    /**
     * 发送私聊文本消息
     */
    public String sendPrivateText(String userId, String text) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message", text);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送私聊图片
     */
    public String sendPrivateImage(String userId, String imageUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("image", imageUrl);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送私聊系统表情
     */
    public String sendPrivateFace(String userId, String faceId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("face_id", faceId);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送私聊JSON消息
     */
    public String sendPrivateJson(String userId, String json) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("json", json);
        return makeRequest("/send_private_json", params);
    }

    /**
     * 发送私聊语音
     */
    public String sendPrivateVoice(String userId, String voiceUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("voice", voiceUrl);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送私聊视频
     */
    public String sendPrivateVideo(String userId, String videoUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("video", videoUrl);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送私聊回复消息
     */
    public String sendPrivateReply(String userId, String messageId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message_id", messageId);
        params.put("message", message);
        return makeRequest("/send_private_msg", params);
    }

    /**
     * 发送私聊音乐卡片
     */
    public String sendPrivateMusic(String userId, String musicId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("music_id", musicId);
        return makeRequest("/send_private_music", params);
    }

    /**
     * 发送私聊自定义音乐卡片
     */
    public String sendPrivateCustomMusic(String userId, Map<String, Object> musicData) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.putAll(musicData);
        return makeRequest("/send_private_custom_music", params);
    }

    /**
     * 发送私聊骰子
     */
    public String sendPrivateDice(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/send_private_dice", params);
    }

    /**
     * 发送私聊猜拳
     */
    public String sendPrivateRPS(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/send_private_rps", params);
    }

    /**
     * 发送私聊合并转发消息
     */
    public String sendPrivateForward(String userId, String messages) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("messages", messages);
        return makeRequest("/send_private_forward_msg", params);
    }

    /**
     * 消息转发到私聊
     */
    public String forwardToPrivate(String userId, String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message_id", messageId);
        return makeRequest("/forward_to_private", params);
    }

    /**
     * 发送私聊文件
     */
    public String sendPrivateFile(String userId, String fileUrl) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("file", fileUrl);
        return makeRequest("/send_private_file", params);
    }

    /**
     * 发送私聊戳一戳
     */
    public String sendPrivatePoke(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/send_private_poke", params);
    }

    // ==================== 通用消息方法 ====================

    /**
     * 发送戳一戳
     */
    public String sendPoke(String targetId, boolean isGroup) throws IOException {
        Map<String, Object> params = new HashMap<>();
        if (isGroup) {
            params.put("group_id", targetId);
        } else {
            params.put("user_id", targetId);
        }
        return makeRequest("/send_poke", params);
    }

    /**
     * 撤回消息
     */
    public String deleteMessage(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/delete_msg", params);
    }

    /**
     * 获取群历史消息
     */
    public String getGroupHistory(String groupId, Integer count) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("count", count);
        return makeRequest("/get_group_msg_history", params);
    }

    /**
     * 获取消息详情
     */
    public String getMessageDetail(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/get_msg", params);
    }

    /**
     * 获取合并转发消息
     */
    public String getForwardMessage(String id) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return makeRequest("/get_forward_msg", params);
    }

    /**
     * 贴表情
     */
    public String setMessageEmotion(String messageId, String emotionId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        params.put("emotion_id", emotionId);
        return makeRequest("/set_msg_emotion", params);
    }

    /**
     * 获取好友历史消息
     */
    public String getFriendHistory(String userId, Integer count) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("count", count);
        return makeRequest("/get_friend_msg_history", params);
    }

    /**
     * 获取贴表情详情
     */
    public String getMessageEmotionDetail(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/get_msg_emotion", params);
    }

    /**
     * 发送合并转发消息
     */
    public String sendForwardMessage(Map<String, Object> forwardData) throws IOException {
        return makeRequest("/send_forward_msg", forwardData);
    }

    /**
     * 获取语音消息详情
     */
    public String getVoiceMessageDetail(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/get_record", params);
    }

    /**
     * 获取图片消息详情
     */
    public String getImageMessageDetail(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/get_image", params);
    }

    /**
     * 发送群AI语音
     */
    public String sendGroupAIVoice(String groupId, String text, String voiceType) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("text", text);
        params.put("voice_type", voiceType);
        return makeRequest("/send_group_ai_voice", params);
    }

    // ==================== 群聊管理相关方法 ====================

    /**
     * 设置群搜索
     */
    public String setGroupSearch(String groupId, boolean allowSearch) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("allow_search", allowSearch);
        return makeRequest("/set_group_search", params);
    }

    /**
     * 获取群详细信息
     */
    public String getGroupDetailInfo(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_detail_info", params);
    }

    /**
     * 设置群添加选项
     */
    public String setGroupAddOption(String groupId, String option) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("option", option);
        return makeRequest("/set_group_add_option", params);
    }

    /**
     * 设置群机器人添加选项
     */
    public String setGroupBotAddOption(String groupId, String option) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("option", option);
        return makeRequest("/set_group_bot_add_option", params);
    }

    /**
     * 批量踢出群成员
     */
    public String kickGroupMembers(String groupId, String[] userIds, boolean rejectRequest) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_ids", userIds);
        params.put("reject_add_request", rejectRequest);
        return makeRequest("/kick_group_members", params);
    }

    /**
     * 设置群备注
     */
    public String setGroupRemark(String groupId, String remark) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("remark", remark);
        return makeRequest("/set_group_remark", params);
    }

    /**
     * 群踢人
     */
    public String kickGroupMember(String groupId, String userId, boolean rejectRequest) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("reject_add_request", rejectRequest);
        return makeRequest("/set_group_kick", params);
    }

    /**
     * 获取群系统消息
     */
    public String getGroupSystemMessages() throws IOException {
        return makeRequest("/get_group_system_msg", new HashMap<>());
    }

    /**
     * 群禁言
     */
    public String setGroupBan(String groupId, String userId, Integer duration) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("duration", duration);
        return makeRequest("/set_group_ban", params);
    }

    /**
     * 获取群精华消息
     */
    public String getGroupEssenceMessages(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_essence_msg_list", params);
    }

    /**
     * 全体禁言
     */
    public String setGroupWholeBan(String groupId, boolean enable) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("enable", enable);
        return makeRequest("/set_group_whole_ban", params);
    }

    /**
     * 设置群头像
     */
    public String setGroupAvatar(String groupId, String image) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("image", image);
        return makeRequest("/set_group_avatar", params);
    }

    /**
     * 设置群管理
     */
    public String setGroupAdmin(String groupId, String userId, boolean enable) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("enable", enable);
        return makeRequest("/set_group_admin", params);
    }

    /**
     * 设置群成员名片
     */
    public String setGroupCard(String groupId, String userId, String card) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("card", card);
        return makeRequest("/set_group_card", params);
    }

    /**
     * 设置群精华消息
     */
    public String setGroupEssence(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/set_essence_msg", params);
    }

    /**
     * 设置群名
     */
    public String setGroupName(String groupId, String groupName) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("group_name", groupName);
        return makeRequest("/set_group_name", params);
    }

    /**
     * 删除群精华消息
     */
    public String deleteGroupEssence(String messageId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return makeRequest("/delete_essence_msg", params);
    }

    /**
     * 退群
     */
    public String leaveGroup(String groupId, boolean isDismiss) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("is_dismiss", isDismiss);
        return makeRequest("/set_group_leave", params);
    }

    /**
     * 发送群公告
     */
    public String sendGroupNotice(String groupId, String content) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("content", content);
        return makeRequest("/_send_group_notice", params);
    }

    /**
     * 设置群头衔
     */
    public String setGroupSpecialTitle(String groupId, String userId, String specialTitle) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("special_title", specialTitle);
        return makeRequest("/set_group_special_title", params);
    }

    /**
     * 获取群公告
     */
    public String getGroupNotices(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/_get_group_notice", params);
    }

    /**
     * 处理加群请求
     */
    public String handleGroupRequest(String flag, String type, boolean approve, String reason) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("flag", flag);
        params.put("type", type);
        params.put("approve", approve);
        params.put("reason", reason);
        return makeRequest("/set_group_add_request", params);
    }

    /**
     * 获取群信息
     */
    public String getGroupInfo(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_info", params);
    }

    /**
     * 获取群列表
     */
    public String getGroupList() throws IOException {
        return makeRequest("/get_group_list", new HashMap<>());
    }

    /**
     * 删除群公告
     */
    public String deleteGroupNotice(String groupId, String noticeId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("notice_id", noticeId);
        return makeRequest("/_delete_group_notice", params);
    }

    /**
     * 获取群成员信息
     */
    public String getGroupMemberInfo(String groupId, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        return makeRequest("/get_group_member_info", params);
    }

    /**
     * 获取群成员列表
     */
    public String getGroupMemberList(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_member_list", params);
    }

    /**
     * 获取群荣誉信息
     * type: talkative-龙王, performer-群聊之火, legend-群聊炽焰, strong_newbie-冒尖小春笋, emotion-快乐之源
     */
    public String getGroupHonor(String groupId, String type) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("type", type);
        return makeRequest("/get_group_honor_info", params);
    }

    /**
     * 获取群扩展信息
     */
    public String getGroupInfoEx(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_info_ex", params);
    }

    /**
     * 获取群@全体成员剩余次数
     */
    public String getGroupAtAllRemain(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_at_all_remain", params);
    }

    /**
     * 获取群禁言列表
     */
    public String getGroupBanList(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_ban_list", params);
    }

    /**
     * 获取群过滤系统消息
     */
    public String getGroupFilteredSystemMessages(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_filtered_system_msg", params);
    }

    /**
     * 群打卡
     */
    public String groupSignIn(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/group_sign_in", params);
    }

    /**
     * 设置群代办
     */
    public String setGroupTodo(String groupId, String todo) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("todo", todo);
        return makeRequest("/set_group_todo", params);
    }

    // ==================== 文件相关方法 ====================

    /**
     * 移动群文件
     */
    public String moveGroupFile(String groupId, String fileId, String parentId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        params.put("parent_id", parentId);
        return makeRequest("/move_group_file", params);
    }

    /**
     * 转存为永久文件
     */
    public String saveFilePermanent(String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("file_id", fileId);
        return makeRequest("/save_file_permanent", params);
    }

    /**
     * 重命名群文件
     */
    public String renameGroupFile(String groupId, String fileId, String newName) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        params.put("new_name", newName);
        return makeRequest("/rename_group_file", params);
    }

    /**
     * 获取文件信息
     */
    public String getFileInfo(String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("file_id", fileId);
        return makeRequest("/get_file_info", params);
    }

    /**
     * 上传群文件
     */
    public String uploadGroupFile(String groupId, String file, String name) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file", file);
        params.put("name", name);
        return makeRequest("/upload_group_file", params);
    }

    /**
     * 创建群文件文件夹
     */
    public String createGroupFolder(String groupId, String name) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("name", name);
        return makeRequest("/create_group_folder", params);
    }

    /**
     * 删除群文件
     */
    public String deleteGroupFile(String groupId, String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        return makeRequest("/delete_group_file", params);
    }

    /**
     * 删除群文件夹
     */
    public String deleteGroupFolder(String groupId, String folderId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("folder_id", folderId);
        return makeRequest("/delete_group_folder", params);
    }

    /**
     * 上传私聊文件
     */
    public String uploadPrivateFile(String userId, String file, String name) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("file", file);
        params.put("name", name);
        return makeRequest("/upload_private_file", params);
    }

    /**
     * 获取群文件系统信息
     */
    public String getGroupFileSystemInfo(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_file_system_info", params);
    }

    /**
     * 下载文件到缓存目录
     */
    public String downloadFile(String url, String headers, String threadCount) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        params.put("headers", headers);
        params.put("thread_count", threadCount);
        return makeRequest("/download_file", params);
    }

    /**
     * 获取群根目录文件列表
     */
    public String getGroupRootFiles(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_root_files", params);
    }

    /**
     * 获取群子目录文件列表
     */
    public String getGroupFilesByFolder(String groupId, String folderId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("folder_id", folderId);
        return makeRequest("/get_group_files_by_folder", params);
    }

    /**
     * 获取群文件链接
     */
    public String getGroupFileUrl(String groupId, String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        return makeRequest("/get_group_file_url", params);
    }

    /**
     * 获取私聊文件链接
     */
    public String getPrivateFileUrl(String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("file_id", fileId);
        return makeRequest("/get_private_file_url", params);
    }

    /**
     * 清空缓存
     */
    public String clearCache() throws IOException {
        return makeRequest("/clear_cache", new HashMap<>());
    }

    // ==================== 群相册相关方法 ====================

    /**
     * 删除群相册文件
     */
    public String deleteGroupAlbumFile(String groupId, String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        return makeRequest("/delete_group_album_file", params);
    }

    /**
     * 点赞群相册
     */
    public String likeGroupAlbum(String groupId, String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        return makeRequest("/like_group_album", params);
    }

    /**
     * 查看群相册评论
     */
    public String viewGroupAlbumComments(String groupId, String fileId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("file_id", fileId);
        return makeRequest("/view_group_album_comments", params);
    }

    /**
     * 获取群相册列表
     */
    public String getGroupAlbumList(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_album_list", params);
    }

    /**
     * 上传图片到群相册
     */
    public String uploadImageToGroupAlbum(String groupId, String image) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("image", image);
        return makeRequest("/upload_image_to_group_album", params);
    }

    /**
     * 获取群相册总列表
     */
    public String getGroupAlbumTotalList(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        return makeRequest("/get_group_album_total_list", params);
    }

    // ==================== 密钥相关方法 ====================

    /**
     * 获取clientkey
     */
    public String getClientKey() throws IOException {
        return makeRequest("/get_clientkey", new HashMap<>());
    }

    /**
     * 获取cookies
     */
    public String getCookies() throws IOException {
        return makeRequest("/get_cookies", new HashMap<>());
    }

    /**
     * 获取 CSRF Token
     */
    public String getCsrfToken() throws IOException {
        return makeRequest("/get_csrf_token", new HashMap<>());
    }

    // ==================== 系统操作相关方法 ====================
    /**
     * 获取机器人账号范围
     */
    public String getRobotAccountRange() throws IOException {
        return makeRequest("/get_robot_account_range", new HashMap<>());
    }

    /**
     * 账号退出
     */
    public String accountExit() throws IOException {
        return makeRequest("/account_exit", new HashMap<>());
    }

    // ==================== 个人操作相关方法 ====================

    /**
     * 获取陌生人信息
     */
    public String getStrangerInfo(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/get_stranger_info", params);
    }

    /**
     * 获取好友信息
     */
    public String getFriendInfo(String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return makeRequest("/get_friend_info", params);
    }

    /**
     * 获取 QQ 相关接口凭证
     */
    public String getCredentials() throws IOException {
        return makeRequest("/get_credentials", new HashMap<>());
    }

    /**
     * nc获取rkey
     */
    public String getNcRkey() throws IOException {
        return makeRequest("/nc_get_rkey", new HashMap<>());
    }

    /**
     * 获取rkey
     */
    public String getRkey() throws IOException {
        return makeRequest("/get_rkey", new HashMap<>());
    }

    /**
     * 获取rkey服务
     */
    public String getRkeyService() throws IOException {
        return makeRequest("/get_rkey_service", new HashMap<>());
    }

    // ==================== 个人操作方法 ====================

    /**
     * OCR 图片识别
     */
    public String ocrImage(String image) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("image", image);
        return makeRequest("/ocr_image", params);
    }

    /**
     * 英译中
     */
    public String translateEnToZh(String text) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("text", text);
        return makeRequest("/translate_en_to_zh", params);
    }

    /**
     * 设置输入状态
     */
    public String setInputStatus(String status) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        return makeRequest("/set_input_status", params);
    }

    /**
     * 检查是否可以发送图片
     */
    public String canSendImage() throws IOException {
        return makeRequest("/can_send_image", new HashMap<>());
    }

    /**
     * 检查是否可以发送语音
     */
    public String canSendRecord() throws IOException {
        return makeRequest("/can_send_record", new HashMap<>());
    }

    /**
     * 获取AI语音人物
     */
    public String getAIVoiceCharacters() throws IOException {
        return makeRequest("/get_ai_voice_characters", new HashMap<>());
    }

    /**
     * 点击按钮
     */
    public String clickButton(String buttonId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("button_id", buttonId);
        return makeRequest("/click_button", params);
    }

    /**
     * 获取AI语音
     */
    public String getAIVoice(String text, String character) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("text", text);
        params.put("character", character);
        return makeRequest("/get_ai_voice", params);
    }

    // ==================== 系统操作方法 ====================

    /**
     * 获取机器人账号范围
     */
    public String getBotAccounts() throws IOException {
        return makeRequest("/get_bot_accounts", new HashMap<>());
    }

    /**
     * 账号退出
     */
    public String logout() throws IOException {
        return makeRequest("/logout", new HashMap<>());
    }

    /**
     * 发送自定义组包
     */
    public String sendCustomPacket(String packet) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("packet", packet);
        return makeRequest("/send_custom_packet", params);
    }

    /**
     * 获取packet状态
     */
    public String getPacketStatus() throws IOException {
        return makeRequest("/get_packet_status", new HashMap<>());
    }

    /**
     * 获取版本信息
     */
    public String getVersionInfo() throws IOException {
        return makeRequest("/get_version_info", new HashMap<>());
    }

    // ==================== 其他方法 ====================

    /**
     * 未知接口
     */
    public String unknown() throws IOException {
        return makeRequest("/unknown", new HashMap<>());
    }

    /**
     * 获取频道列表
     */
    public String getGuildList() throws IOException {
        return makeRequest("/get_guild_list", new HashMap<>());
    }

    /**
     * 获取频道服务资料
     */
    public String getGuildServiceProfile() throws IOException {
        return makeRequest("/get_guild_service_profile", new HashMap<>());
    }

    /**
     * 检查链接安全性
     */
    public String checkUrlSafety(String url) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        return makeRequest("/check_url_safety", params);
    }

    /**
     * 获取收藏列表
     */
    public String getFavoriteList() throws IOException {
        return makeRequest("/get_favorite_list", new HashMap<>());
    }

    /**
     * 获取被过滤的加群请求
     */
    public String getFilteredGroupRequests() throws IOException {
        return makeRequest("/get_filtered_group_requests", new HashMap<>());
    }
}