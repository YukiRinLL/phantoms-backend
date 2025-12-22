package com.phantoms.phantomsbackend.common.utils;

import okhttp3.*;
import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.SystemConfigService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RisingStonesUtils {

    private static final Logger logger = Logger.getLogger(RisingStonesUtils.class.getName());
    private static final String BASE_URL = "https://apiff14risingstones.web.sdo.com/api/home/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";
    private static final String REFERER = "https://ff14risingstones.web.sdo.com/";

    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    /**
     * 获取SystemConfigService实例
     */
    private static SystemConfigService getSystemConfigService() {
        return SpringContextHolder.getBean(SystemConfigService.class);
    }

    public static JSONObject getUserInfo(String uuid) throws IOException {
        String cookies = getSystemConfigService().getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            logger.log(Level.SEVERE, "未找到登录cookies，请先登录");
            throw new IOException("未找到登录cookies，请先登录");
        }
        
        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "userInfo/getUserInfo").newBuilder()
                .addQueryParameter("uuid", uuid)
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        // 创建一个不使用内部cookieJar的新客户端，以避免cookie冲突
        OkHttpClient tempClient = client.newBuilder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) { return Collections.emptyList(); }
            })
            .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting user info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "User info response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    public static JSONObject getGuildInfo(String guildId) throws IOException {
        String cookies = getSystemConfigService().getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            logger.log(Level.SEVERE, "未找到登录cookies，请先登录");
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "guild/getGuildInfo").newBuilder()
                .addQueryParameter("guild_id", guildId)
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        // 创建一个不使用内部cookieJar的新客户端，以避免cookie冲突
        OkHttpClient tempClient = client.newBuilder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) { return Collections.emptyList(); }
            })
            .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting guild info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Guild info response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    public static JSONObject getGuildMember(String guildId) throws IOException {
        String cookies = getSystemConfigService().getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            logger.log(Level.SEVERE, "未找到登录cookies，请先登录");
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "guild/getGuildMember").newBuilder()
                .addQueryParameter("guild_id", guildId)
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        // 创建一个不使用内部cookieJar的新客户端，以避免cookie冲突
        OkHttpClient tempClient = client.newBuilder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) { return Collections.emptyList(); }
            })
            .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting guild member", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Guild member response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }

    private static String getAllCookies(Response response) {
        StringBuilder cookies = new StringBuilder();
        List<String> cookieHeaders = response.headers("Set-Cookie");
        for (String header : cookieHeaders) {
            if (cookies.length() > 0) {
                cookies.append("; ");
            }
            cookies.append(header.split(";")[0]); // Only take the cookie name=value part
        }
        return cookies.toString();
    }

    public static JSONObject getGuildMemberDynamic(String guildId, int page, int limit) throws IOException {
        String cookies = getSystemConfigService().getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            logger.log(Level.SEVERE, "未找到登录cookies，请先登录");
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "guild/guildMemberDynamic").newBuilder()
            .addQueryParameter("guild_id", guildId)
            .addQueryParameter("page", String.valueOf(page))
            .addQueryParameter("limit", String.valueOf(limit))
            .addQueryParameter("tempsuid", tempsuid)
            .build();

        // 创建一个不使用内部cookieJar的新客户端，以避免cookie冲突
        OkHttpClient tempClient = client.newBuilder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) { return Collections.emptyList(); }
            })
            .build();

        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Cookie", cookies)
            .header("Referer", REFERER)
            .build();

        try (Response response = tempClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting guild member dynamic", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String responseBody = response.body().string();
            logger.log(Level.FINE, "Guild member dynamic response: {0}", responseBody);
            return JSONObject.parseObject(responseBody);
        }
    }
}