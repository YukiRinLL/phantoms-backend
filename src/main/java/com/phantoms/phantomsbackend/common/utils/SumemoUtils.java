package com.phantoms.phantomsbackend.common.utils;

import okhttp3.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SumemoUtils {

    private static final Logger logger = Logger.getLogger(SumemoUtils.class.getName());
    private static final String BASE_URL = "https://api.sumemo.dev/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";

    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取玩家特定副本的最佳攻略信息
     * @param playerName 玩家名称
     * @param serverName 服务器名称
     * @param zoneId 副本ID
     * @return 最佳攻略信息
     */
    public static JSONObject getPlayerBestDuty(String playerName, String serverName, int zoneId) throws IOException {
        String formattedName = String.format("%s@%s", playerName, serverName);
        HttpUrl url = HttpUrl.parse(BASE_URL + "member/" + formattedName + "/" + zoneId + "/best").newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting player best duty", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Player best duty response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    /**
     * 获取玩家特定副本的最新攻略信息列表
     * @param playerName 玩家名称
     * @param serverName 服务器名称
     * @param zoneId 副本ID
     * @param limit 返回数量限制
     * @return 最新攻略信息列表
     */
    public static JSONArray getPlayerLatestDuty(String playerName, String serverName, int zoneId, int limit) throws IOException {
        String formattedName = String.format("%s@%s", playerName, serverName);
        HttpUrl url = HttpUrl.parse(BASE_URL + "member/" + formattedName + "/" + zoneId + "/latest").newBuilder()
                .addQueryParameter("limit", String.valueOf(limit))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting player latest duty", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Player latest duty response: {0}", responseBody);
            return JSONArray.parseArray(responseBody);
        }
    }

    /**
     * 获取副本信息
     * @param zoneId 副本ID
     * @return 副本信息
     */
    public static JSONObject getDutyInfo(int zoneId) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "duty/" + zoneId).newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting duty info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Duty info response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    /**
     * 获取副本名称
     * @param zoneId 副本ID
     * @return 副本名称
     */
    public static String getDutyName(int zoneId) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "duty/" + zoneId + "/name").newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting duty name", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            JSONObject result = JSONObject.parseObject(responseBody);
            return result.getString("name");
        }
    }
}