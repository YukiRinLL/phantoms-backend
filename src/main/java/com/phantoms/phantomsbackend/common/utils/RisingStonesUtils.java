package com.phantoms.phantomsbackend.common.utils;

import okhttp3.*;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RisingStonesUtils {

    private static final Logger logger = Logger.getLogger(RisingStonesUtils.class.getName());

    private static final String BASE_URL = "https://apiff14risingstones.web.sdo.com/api/home/";

    private static final OkHttpClient client = new OkHttpClient();

    public static JSONObject getUserInfo(String uuid, String daoyuToken, String cookie) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "userInfo/getUserInfo").newBuilder()
                .addQueryParameter("uuid", uuid)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2218A Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 DaoYu/9.4.14")
                .header("authorization", daoyuToken)
                .header("accept", "application/json, text/plain, */*")
                .header("accept-encoding", "gzip, deflate")
                .header("Cookie", cookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting user info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "User info response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    public static JSONObject getGuildInfo(String guildId, String daoyuToken, String cookie) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "guild/getGuildInfo").newBuilder()
                .addQueryParameter("guild_id", guildId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2218A Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 DaoYu/9.4.14")
                .header("authorization", daoyuToken)
                .header("accept", "application/json, text/plain, */*")
                .header("accept-encoding", "gzip, deflate")
                .header("Cookie", cookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting guild info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Guild info response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    public static JSONObject getGuildMember(String guildId, String daoyuToken, String cookie) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "guild/getGuildMember").newBuilder()
                .addQueryParameter("guild_id", guildId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2218A Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 DaoYu/9.4.14")
                .header("authorization", daoyuToken)
                .header("accept", "application/json, text/plain, */*")
                .header("accept-encoding", "gzip, deflate")
                .header("Cookie", cookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting guild member", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Guild member response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }
}