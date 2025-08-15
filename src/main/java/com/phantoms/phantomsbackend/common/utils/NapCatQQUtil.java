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
}