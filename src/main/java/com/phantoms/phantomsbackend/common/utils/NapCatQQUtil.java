package com.phantoms.phantomsbackend.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
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
     * 发送私聊消息
     *
     * @param userId 用户 QQ 号
     * @param message 消息内容
     * @throws IOException 如果发生网络错误
     */
    public void sendPrivateMessage(String userId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("message", message);

        String url = httpServerUrl + "/send_private_msg?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 发送群消息
     *
     * @param groupId 群号
     * @param message 消息内容
     * @throws IOException 如果发生网络错误
     */
    public void sendGroupMessage(String groupId, String message) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("message", message);

        String url = httpServerUrl + "/send_group_msg?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 获取好友列表
     *
     * @throws IOException 如果发生网络错误
     */
    public void getFriendList() throws IOException {
        String url = httpServerUrl + "/get_friend_list?access_token=" + accessToken;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 设置自定义在线状态
     *
     * @param faceId 表情 ID
     * @param wording 自定义状态文字
     * @throws IOException 如果发生网络错误
     */
    public void setCustomOnlineStatus(String faceId, String wording) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("face_id", faceId);
        params.put("wording", wording);

        String url = httpServerUrl + "/set_diy_online_status?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 处理好友添加请求
     *
     * @param flag 请求标识
     * @param approve 是否同意
     * @param remark 好友备注
     * @throws IOException 如果发生网络错误
     */
    public void handleFriendRequest(String flag, boolean approve, String remark) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("flag", flag);
        params.put("approve", approve);
        params.put("remark", remark);

        String url = httpServerUrl + "/set_friend_add_request?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 设置群管理员
     *
     * @param groupId 群号
     * @param userId 用户 QQ 号
     * @param enable 是否设置为管理员
     * @throws IOException 如果发生网络错误
     */
    public void setGroupAdmin(String groupId, String userId, boolean enable) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("enable", enable);

        String url = httpServerUrl + "/set_group_admin?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 移除群成员
     *
     * @param groupId 群号
     * @param userId 用户 QQ 号
     * @param rejectAddRequest 是否拒绝该用户未来的加群请求
     * @throws IOException 如果发生网络错误
     */
    public void removeGroupMember(String groupId, String userId, boolean rejectAddRequest) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("reject_add_request", rejectAddRequest);

        String url = httpServerUrl + "/set_group_kick?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 禁言群成员
     *
     * @param groupId 群号
     * @param userId 用户 QQ 号
     * @param duration 禁言时长（单位：秒）
     * @throws IOException 如果发生网络错误
     */
    public void muteGroupMember(String groupId, String userId, int duration) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("duration", duration);

        String url = httpServerUrl + "/set_group_ban?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 解除群成员禁言
     *
     * @param groupId 群号
     * @param userId 用户 QQ 号
     * @throws IOException 如果发生网络错误
     */
    public void unmuteGroupMember(String groupId, String userId) throws IOException {
        muteGroupMember(groupId, userId, 0);
    }

    /**
     * 获取群成员信息
     *
     * @param groupId 群号
     * @param userId 用户 QQ 号
     * @throws IOException 如果发生网络错误
     */
    public void getGroupMemberInfo(String groupId, String userId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);
        params.put("user_id", userId);

        String url = httpServerUrl + "/get_group_member_info?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }

    /**
     * 获取群成员列表
     *
     * @param groupId 群号
     * @throws IOException 如果发生网络错误
     */
    public void getGroupMemberList(String groupId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("group_id", groupId);

        String url = httpServerUrl + "/get_group_member_list?access_token=" + accessToken;
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
            System.out.println("Response: " + response.body().string());
        }
    }
}