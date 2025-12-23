package com.phantoms.phantomsbackend.common.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.phantoms.phantomsbackend.service.SystemConfigService;

// 二维码解析和生成相关依赖（需要引入ZXing库）
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

@Component
public class RisingStonesSigninHelper {

    private static final Logger logger = Logger.getLogger(RisingStonesSigninHelper.class.getName());
    private static final String BASE_URL = "https://apiff14risingstones.web.sdo.com/api/home/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";
    private static final String REFERER = "https://ff14risingstones.web.sdo.com/";
    private static final String LOGIN_REFERER = "https://login.u.sdo.com/";
    private static final MediaType FORM_DATA = MediaType.parse("application/x-www-form-urlencoded");

    // 登录相关常量
    public static final String SSO_REDIRECT_URL = "http://apiff14risingstones.web.sdo.com/api/home/GHome/login?redirectUrl=https://ff14risingstones.web.sdo.com/pc/index.html";
    public static final String APP_REDIRECT_URL = "https://ff14risingstones.web.sdo.com/pc/index.html";
    public static final String APP_ID = "6788";
    public static final String AREA_ID = "1";

    private OkHttpClient client;
    private final MyCookieJar cookieJar;

    @Autowired
    private SystemConfigService systemConfigService;

    public RisingStonesSigninHelper() {
        this.cookieJar = new MyCookieJar();
        // 初始化必要的cookies
        String userID = "445385824-" + Math.round(899999999 * Math.random() + 1E9) + "-" + Math.round(System.currentTimeMillis() / 1000);
        cookieJar.saveFromResponse(HttpUrl.parse("https://apiff14risingstones.web.sdo.com/"), 
            Collections.singletonList(Cookie.parse(HttpUrl.parse("https://apiff14risingstones.web.sdo.com/"), "__wftflow=1607418051=1")));
        cookieJar.saveFromResponse(HttpUrl.parse("https://apiff14risingstones.web.sdo.com/"), 
            Collections.singletonList(Cookie.parse(HttpUrl.parse("https://apiff14risingstones.web.sdo.com/"), "userinfo=userid=" + userID + "&siteid=SDG-08132-01")));

        client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .build();
    }

    // 自定义CookieJar实现
    private static class MyCookieJar implements CookieJar {
        private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            String host = url.host();
            List<Cookie> existingCookies = cookieStore.getOrDefault(host, new ArrayList<>());
            
            // 合并现有cookies和新cookies，保留最新的同名称cookie
            Map<String, Cookie> cookieMap = new HashMap<>();
            for (Cookie cookie : existingCookies) {
                cookieMap.put(cookie.name(), cookie);
            }
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.name(), cookie);
            }
            
            cookieStore.put(host, new ArrayList<>(cookieMap.values()));
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : Collections.emptyList();
        }

        public String getCookieString(String url) {
            HttpUrl httpUrl = HttpUrl.parse(url);
            if (httpUrl == null) return "";

            List<Cookie> cookies = cookieStore.get(httpUrl.host());
            if (cookies == null || cookies.isEmpty()) return "";

            StringBuilder sb = new StringBuilder();
            for (Cookie cookie : cookies) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(cookie.name()).append("=").append(cookie.value());
            }
            return sb.toString();
        }
    }

    /**
     * 获取登录二维码
     */
    public String getLoginQRCode() throws IOException, NotFoundException {
        HttpUrl url = HttpUrl.parse("https://w.cas.sdo.com/authen/getcodekey.jsonp").newBuilder()
            .addQueryParameter("appId", APP_ID)
            .addQueryParameter("areaId", AREA_ID)
            .addQueryParameter("maxsize", "145")
            .addQueryParameter("r", String.valueOf(Math.random()))
            .build();

        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Referer", LOGIN_REFERER)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response.code());

            // 解析二维码
            ResponseBody body = response.body();
            if (body == null) throw new IOException("Empty response body");

            BufferedImage image = ImageIO.read(body.byteStream());
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(image)));

            Result result = new MultiFormatReader().decode(binaryBitmap);
            return result.getText();
        }
    }

    /**
     * 获取登录信息
     */
    public JSONObject getLoginInfo(String serviceURL) throws IOException {
        HttpUrl url = HttpUrl.parse("https://w.cas.sdo.com/authen/codeKeyLogin.jsonp").newBuilder()
            .addQueryParameter("appId", APP_ID)
            .addQueryParameter("areaId", AREA_ID)
            .addQueryParameter("serviceUrl", serviceURL)
            .addQueryParameter("callback", "codeKeyLogin_JSONPMethod")
            .addQueryParameter("code", "300")
            .addQueryParameter("productId", "2")
            .addQueryParameter("productVersion", "3.1.0")
            .addQueryParameter("authenSource", "2")
            .addQueryParameter("_", String.valueOf(System.currentTimeMillis()))
            .build();

        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Referer", LOGIN_REFERER)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response.code());

            String responseBody = response.body().string();
            // 解析JSONP响应
            Pattern pattern = Pattern.compile("^codeKeyLogin_JSONPMethod\\((.+)\\)$");
            Matcher matcher = pattern.matcher(responseBody);
            if (!matcher.find()) throw new IOException("Failed to parse JSONP response");

            String jsonStr = matcher.group(1);
            return JSONObject.parseObject(jsonStr);
        }
    }

//    /**
//     * 等待二维码扫描登录
//     */
//    public String waitLoginQRCode() throws IOException, NotFoundException, InterruptedException {
//        String loginQRCode = getLoginQRCode();
//        logger.log(Level.INFO, "登录二维码内容: {0}", loginQRCode);
//        logger.log(Level.INFO, "请使用叨鱼扫描二维码登录");
//
//        long startTime = System.currentTimeMillis();
//        while (System.currentTimeMillis() - startTime < 60 * 1000) { // 等待60秒
//            JSONObject loginInfo = getLoginInfo(SSO_REDIRECT_URL);
//            JSONObject data = loginInfo.getJSONObject("data");
//
//            if (data.containsKey("ticket")) {
//                return data.getString("ticket");
//            }
//
//            if (data.containsKey("mappedErrorCode") && data.getInteger("mappedErrorCode") == -10515801) {
//                throw new IOException("二维码已失效");
//            }
//
//            Thread.sleep(1000); // 每秒检查一次
//        }
//
//        throw new IOException("二维码扫描超时");
//    }

    /**
     * 完成登录流程
     */
    public void finishLogin(String ticket) throws IOException {
        // 使用与TypeScript代码完全相同的URL格式和协议
        HttpUrl url = HttpUrl.parse("http://apiff14risingstones.web.sdo.com/api/home/GHome/login").newBuilder()
            .addQueryParameter("ticket", ticket)
            .addQueryParameter("redirectUrl", APP_REDIRECT_URL)
            .build();

        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Referer", LOGIN_REFERER)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response.code());
            // 获取cookies并保存到数据库
            String cookies = getCookies();
            systemConfigService.updateLoginCookies(cookies);
        }
    }

//    /**
//     * 执行完整的登录流程
//     */
//    public String login() throws IOException, NotFoundException, InterruptedException {
//        String ticket = waitLoginQRCode();
//        finishLogin(ticket);
//        return cookieJar.getCookieString("https://apiff14risingstones.web.sdo.com/");
//    }

    /**
     * 获取当前的cookies
     */
    public String getCookies() {
        return cookieJar.getCookieString("https://apiff14risingstones.web.sdo.com/");
    }

    /**
     * 检查登录状态
     */
    public JSONObject checkLoginStatus() throws IOException {
        String cookies = systemConfigService.getLoginCookies();
        if (cookies == null || cookies.isEmpty()) {
            throw new IOException("未找到登录cookies，请先登录");
        }
        
        String tempsuid = UUID.randomUUID().toString();
        HttpUrl url = HttpUrl.parse(BASE_URL + "GHome/isLogin").newBuilder()
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
                logger.log(Level.SEVERE, "Unexpected code {0} for checkLoginStatus", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            return JSONObject.parseObject(response.body().string());
        }
    }
    
    /**
     * 检查登录状态（兼容旧版接口，保留cookies参数但不使用）
     */
    public JSONObject checkLoginStatus(String cookies) throws IOException {
        return checkLoginStatus();
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getCharacterBindInfo", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for doSignIn", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getSignLog", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getSignInRewardList", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getSignInReward", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for createPostComment", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for createDynamic", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for deleteDynamic", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
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

    /**
     * 辅助方法：格式化Cookie
     */
    public String formatCookies(Map<String, String> cookies) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
}