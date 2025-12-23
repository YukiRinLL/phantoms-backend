package com.phantoms.phantomsbackend.common.utils;

import okhttp3.*;
import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RisingStonesUtils {

    private static final Logger logger = Logger.getLogger(RisingStonesUtils.class.getName());
    private static final String BASE_URL = "https://apiff14risingstones.web.sdo.com/api/home/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";
    private static final String REFERER = "https://ff14risingstones.web.sdo.com/";

    private static final OkHttpClient client;

    @Autowired
    private SystemConfigService systemConfigService;

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

    /**
     * 获取角色绑定信息
     */
    public JSONObject getCharacterBindInfo() throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "groupAndRole/getCharacterBindInfo").newBuilder()
                .addQueryParameter("platform", "1")
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
            logger.log(Level.INFO, "Response code for getCharacterBindInfo: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 获取角色绑定信息（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject getCharacterBindInfo(String cookies) throws IOException {
        return getCharacterBindInfo();
    }

    /**
     * 执行签到
     */
    public JSONObject doSignIn() throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "sign/signIn").newBuilder()
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("tempsuid", tempsuid)
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
                .post(body)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            logger.log(Level.INFO, "Response code for doSignIn: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 执行签到（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject doSignIn(String cookies) throws IOException {
        return doSignIn();
    }

    /**
     * 获取签到日志
     */
    public JSONObject getSignLog(String month) throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "sign/mySignLog").newBuilder()
                .addQueryParameter("month", month)
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
            logger.log(Level.INFO, "Response code for getSignLog: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 获取签到日志（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject getSignLog(String cookies, String month) throws IOException {
        return getSignLog(month);
    }

    /**
     * 获取签到奖励列表
     */
    public JSONObject getSignInRewardList(String month) throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "sign/signRewardList").newBuilder()
                .addQueryParameter("month", month)
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
            logger.log(Level.INFO, "Response code for getSignInRewardList: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 获取签到奖励列表（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject getSignInRewardList(String cookies, String month) throws IOException {
        return getSignInRewardList(month);
    }

    /**
     * 领取签到奖励
     */
    public JSONObject getSignInReward(int id, String month) throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "sign/getSignReward").newBuilder()
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .add("month", month)
                .add("tempsuid", tempsuid)
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
                .post(body)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            logger.log(Level.INFO, "Response code for getSignInReward: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 领取签到奖励（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject getSignInReward(String cookies, int id, String month) throws IOException {
        return getSignInReward(id, month);
    }

    /**
     * 创建动态评论
     */
    public JSONObject createPostComment(String content, String posts_id,
                                        String parent_id, String root_parent, String comment_pic) throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "posts/comment").newBuilder()
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("content", content)
                .add("posts_id", posts_id)
                .add("parent_id", parent_id)
                .add("root_parent", root_parent)
                .add("comment_pic", comment_pic)
                .add("tempsuid", tempsuid)
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
                .post(body)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            logger.log(Level.INFO, "Response code for createPostComment: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 创建动态评论（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject createPostComment(String cookies, String content, String posts_id,
                                        String parent_id, String root_parent, String comment_pic) throws IOException {
        return createPostComment(content, posts_id, parent_id, root_parent, comment_pic);
    }

    /**
     * 创建动态
     */
    public JSONObject createDynamic(String content, int scope, String pic_url) throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "dynamic/create").newBuilder()
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("content", content)
                .add("scope", String.valueOf(scope))
                .add("pic_url", pic_url)
                .add("tempsuid", tempsuid)
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
                .post(body)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = tempClient.newCall(request).execute()) {
            logger.log(Level.INFO, "Response code for createDynamic: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 创建动态（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject createDynamic(String cookies, String content, int scope, String pic_url) throws IOException {
        return createDynamic(content, scope, pic_url);
    }

    /**
     * 删除动态
     */
    public JSONObject deleteDynamic(int dynamic_id) throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }

        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "dynamic/deleteDynamic").newBuilder()
                .addQueryParameter("tempsuid", tempsuid)
                .build();

        JSONObject requestBody = new JSONObject();
        requestBody.put("dynamic_id", dynamic_id);

        RequestBody body = RequestBody.create(
                requestBody.toJSONString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .header("User-Agent", USER_AGENT)
                .header("Cookie", cookies)
                .header("Referer", REFERER)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            logger.log(Level.INFO, "Response code for deleteDynamic: {0}", response.code());
            return JSONObject.parseObject(response.body().string());
        }
    }

    /**
     * 删除动态（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject deleteDynamic(String cookies, int dynamic_id) throws IOException {
        return deleteDynamic(dynamic_id);
    }

    /**
     * 辅助方法：解析Cookie头
     */
    public Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookies;
        }

        String[] parts = cookieHeader.split("; ");
        for (String part : parts) {
            int equalsIndex = part.indexOf('=');
            if (equalsIndex > 0 && equalsIndex < part.length() - 1) {
                String name = part.substring(0, equalsIndex);
                String value = part.substring(equalsIndex + 1);
                cookies.put(name, value);
            }
        }
        return cookies;
    }
}