package com.phantoms.phantomsbackend.common.utils;

import com.alibaba.fastjson.JSONArray;
import okhttp3.*;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RisingStonesLoginTool {

    private static final Logger logger = Logger.getLogger(RisingStonesLoginTool.class.getName());
    private static final String BASE_URL = "https://apiff14risingstones.web.sdo.com/api/home/";
    private static final String DAO_URL = "https://daoyu.sdo.com/api/thirdPartyAuth/";
    private static final String DAOGU_KEY = "DY_6FE0F3F7C9CA488F86E2BE9BD907C657";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String[] getDaoYuTokenAndCookie() {
        try {
            // Step 1: Get Temp Cookies
            Map<String, String> cookies = getTempCookies();
            logger.log(Level.INFO, "Temp Cookies: {0}", cookies);

            // Step 2: Initialize Login Flow and get flowId
            String flowId = initializeLoginFlow(cookies);
            logger.log(Level.INFO, "Flow ID: {0}", flowId);

            // Step 3: Query Account List
            String accountId = queryAccountList(flowId, cookies);
            logger.log(Level.INFO, "Account ID: {0}", accountId);

            // Step 4: Make Confirm
            boolean confirmResult = makeConfirm(flowId, accountId, cookies);
            logger.log(Level.INFO, "Confirm Result: {0}", confirmResult);

            if (confirmResult) {
                // Step 5: Get Sub Account Key
                String subAccountKey = getSubAccountKey(flowId, cookies);
                logger.log(Level.INFO, "Sub Account Key: {0}", subAccountKey);

                // Step 6: Dao Login
                String[] loginResult = daoLogin(subAccountKey, cookies);
                String daoyuToken = loginResult[0];

                // 假设 loginResult[1] 是一个以 "; " 分隔的 Cookie 字符串
                String[] cookieStrings = loginResult[1].split("; ");
                List<String> cookieList = Arrays.asList(cookieStrings);
                Map<String, String> updatedCookies = parseCookies(cookieList);

                logger.log(Level.INFO, "DaoYu Token: {0}", daoyuToken);
                logger.log(Level.INFO, "Updated Cookies: {0}", updatedCookies);

                // Step 7: Get Character Bind Info
                boolean bindInfoStatus = getCharacterBindInfo(daoyuToken, updatedCookies);
                logger.log(Level.INFO, "Character Bind Info Status: {0}", bindInfoStatus);

                return new String[]{daoyuToken, formatCookies(updatedCookies)};
            } else {
                logger.log(Level.SEVERE, "Confirm failed. Unable to obtain DaoYu Token.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred during the login process", e);
        }
        return new String[]{null, null};
    }

    private static Map<String, String> getTempCookies() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "GHome/isLogin")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2218A Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 DaoYu/9.4.14")
                .header("accept", "application/json, text/plain, */*")
//                .header("accept-encoding", "gzip, deflate")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting temp cookies", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            return parseCookies(response.headers("Set-Cookie"));
        }
    }

    private static String initializeLoginFlow(Map<String, String> cookies) throws IOException {
        String device_id = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        String device_manuid = generateRandomString(6);

        HttpUrl url = HttpUrl.parse(DAO_URL + "initialize").newBuilder()
                .addQueryParameter("src_code", "4")
                .addQueryParameter("app_version", "9.4.14")
                .addQueryParameter("app_version_code", "688")
                .addQueryParameter("device_gid", "_08:ff:3d:32:60:00")
                .addQueryParameter("device_os", "12")
                .addQueryParameter("device_manufacturer", "vivo")
                .addQueryParameter("device_txzDeviceId", "")
                .addQueryParameter("_dispath", "0")
                .addQueryParameter("clientId", "ff14risingstones")
                .addQueryParameter("appId", "6788")
                .addQueryParameter("scope", "get_account_profile")
                .addQueryParameter("extend", "")
                .addQueryParameter("scene", "")
                .addQueryParameter("device_id", device_id)
                .addQueryParameter("device_manuid", device_manuid)
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "okhttp/2.5.0")
//                .header("accept-encoding", "gzip")
                .header("Cookie", formatCookies(cookies))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for initializing login flow", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            cookies.putAll(parseCookies(response.headers("Set-Cookie")));
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Initialize login flow response: {0}", jsonResponse.toJSONString());
            return jsonResponse.getJSONObject("data").getString("flowId");
        }
    }

    private static String queryAccountList(String flowId, Map<String, String> cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(DAO_URL + "queryAccountList").newBuilder()
                .addQueryParameter("src_code", "4")
                .addQueryParameter("app_version", "9.4.14")
                .addQueryParameter("app_version_code", "688")
                .addQueryParameter("device_gid", "_08:ff:3d:32:60:00")
                .addQueryParameter("device_os", "12")
                .addQueryParameter("device_manufacturer", "vivo")
                .addQueryParameter("device_txzDeviceId", "")
                .addQueryParameter("_dispath", "0")
                .addQueryParameter("clientId", "ff14risingstones")
                .addQueryParameter("appId", "6788")
                .addQueryParameter("scope", "get_account_profile")
                .addQueryParameter("extend", "")
                .addQueryParameter("scene", "")
                .addQueryParameter("device_id", UUID.randomUUID().toString().toUpperCase().replace("-", ""))
                .addQueryParameter("device_manuid", generateRandomString(6))
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .addQueryParameter("flowId", flowId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "okhttp/2.5.0")
//                .header("accept-encoding", "gzip")
                .header("Cookie", formatCookies(cookies))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for querying account list", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            cookies.putAll(parseCookies(response.headers("Set-Cookie")));
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Query account list response: {0}", jsonResponse.toJSONString());
            JSONArray accountList = jsonResponse.getJSONObject("data").getJSONArray("accountList");
            return accountList.getJSONObject(0).getString("accountId");
        }
    }

    private static boolean makeConfirm(String flowId, String accountId, Map<String, String> cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(DAO_URL + "chooseAccount").newBuilder()
                .addQueryParameter("src_code", "4")
                .addQueryParameter("app_version", "9.4.14")
                .addQueryParameter("app_version_code", "688")
                .addQueryParameter("device_gid", "_08:ff:3d:32:60:00")
                .addQueryParameter("device_os", "12")
                .addQueryParameter("device_manufacturer", "vivo")
                .addQueryParameter("device_txzDeviceId", "")
                .addQueryParameter("_dispath", "0")
                .addQueryParameter("clientId", "ff14risingstones")
                .addQueryParameter("appId", "6788")
                .addQueryParameter("scope", "get_account_profile")
                .addQueryParameter("extend", "")
                .addQueryParameter("scene", "")
                .addQueryParameter("device_id", UUID.randomUUID().toString().toUpperCase().replace("-", ""))
                .addQueryParameter("device_manuid", generateRandomString(6))
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .addQueryParameter("flowId", flowId)
                .addQueryParameter("accountId", accountId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "okhttp/2.5.0")
//                .header("accept-encoding", "gzip")
                .header("Cookie", formatCookies(cookies))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for making confirm", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            cookies.putAll(parseCookies(response.headers("Set-Cookie")));
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Make confirm response: {0}", jsonResponse.toJSONString());
            return "success".equals(jsonResponse.getString("return_message"));
        }
    }

    private static String getSubAccountKey(String flowId, Map<String, String> cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(DAO_URL + "confirm").newBuilder()
                .addQueryParameter("src_code", "4")
                .addQueryParameter("app_version", "9.4.14")
                .addQueryParameter("app_version_code", "688")
                .addQueryParameter("device_gid", "_08:ff:3d:32:60:00")
                .addQueryParameter("device_os", "12")
                .addQueryParameter("device_manufacturer", "vivo")
                .addQueryParameter("device_txzDeviceId", "")
                .addQueryParameter("_dispath", "0")
                .addQueryParameter("clientId", "ff14risingstones")
                .addQueryParameter("appId", "6788")
                .addQueryParameter("scope", "get_account_profile")
                .addQueryParameter("extend", "")
                .addQueryParameter("scene", "")
                .addQueryParameter("device_id", UUID.randomUUID().toString().toUpperCase().replace("-", ""))
                .addQueryParameter("device_manuid", generateRandomString(6))
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .addQueryParameter("flowId", flowId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "okhttp/2.5.0")
//                .header("accept-encoding", "gzip")
                .header("Cookie", formatCookies(cookies))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting sub account key", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            cookies.putAll(parseCookies(response.headers("Set-Cookie")));
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Get sub account key response: {0}", jsonResponse.toJSONString());
            return jsonResponse.getJSONObject("data").getString("authorization");
        }
    }

    private static String[] daoLogin(String subAccountKey, Map<String, String> cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "GHome/daoLogin").newBuilder()
                .addQueryParameter("authorization", subAccountKey)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2218A Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 DaoYu/9.4.14")
                .header("accept", "application/json, text/plain, */*")
//                .header("accept-encoding", "gzip, deflate")
                .header("Cookie", formatCookies(cookies))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for dao login", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            cookies.putAll(parseCookies(response.headers("Set-Cookie")));
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Dao login response: {0}", jsonResponse.toJSONString());
            String daoyuToken = jsonResponse.getJSONObject("data").getString("DaoyuToken");
            return new String[]{daoyuToken, formatCookies(cookies)};
        }
    }

    private static boolean getCharacterBindInfo(String daoyuToken, Map<String, String> cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "groupAndRole/getCharacterBindInfo").newBuilder()
                .addQueryParameter("platform", "1")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2218A Build/V417IR; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36 DaoYu/9.4.14")
                .header("authorization", daoyuToken)
                .header("accept", "application/json, text/plain, */*")
//                .header("accept-encoding", "gzip, deflate")
                .header("Cookie", formatCookies(cookies))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting character bind info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            cookies.putAll(parseCookies(response.headers("Set-Cookie")));
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Character bind info response: {0}", jsonResponse.toJSONString());
            return jsonResponse.getIntValue("code") == 10000;
        }
    }

    private static Map<String, String> parseCookies(List<String> cookieHeaders) {
        Map<String, String> cookies = new HashMap<>();
        for (String header : cookieHeaders) {
            String[] parts = header.split(";")[0].split("=");
            if (parts.length == 2) {
                cookies.put(parts[0].trim(), parts[1].trim());
            }
        }
        return cookies;
    }

    private static String formatCookies(Map<String, String> cookies) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}