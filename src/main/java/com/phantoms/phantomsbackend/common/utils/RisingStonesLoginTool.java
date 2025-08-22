package com.phantoms.phantomsbackend.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RisingStonesLoginTool {

    private static final Logger logger = Logger.getLogger(RisingStonesLoginTool.class.getName());

    private static final String BASE_URL = "https://apiff14risingstones.web.sdo.com/api/home/";
    private static final String DAO_URL = "https://daoyu.sdo.com/api/thirdPartyAuth/";
    private static final String DAOGU_KEY = "DY_6FE0F3F7C9CA488F86E2BE9BD907C657"; // daoyukey

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    // 定义一个成员变量来存储 device_id
    private static String device_id;

    public static String[] getDaoYuTokenAndCookie() {
        try {
            // Step 1: Get Temp Cookies
            String tempCookies = getTempCookies();
            logger.log(Level.INFO, "Temp Cookies: {0}", tempCookies);

            // Step 2: Initialize Login Flow and get flowId
            String flowId = initializeLoginFlow(tempCookies);
            logger.log(Level.INFO, "Flow ID: {0}", flowId);

            // Step 3: Query Account List
            String accountId = queryAccountList(flowId, tempCookies);
            logger.log(Level.INFO, "Account ID: {0}", accountId);

            // Step 4: Make Confirm
            boolean confirmResult = makeConfirm(flowId, accountId, tempCookies);
            logger.log(Level.INFO, "Confirm Result: {0}", confirmResult);

            if (confirmResult) {
                // Step 5: Get Sub Account Key
                String subAccountKey = getSubAccountKey(flowId, tempCookies);
                logger.log(Level.INFO, "Sub Account Key: {0}", subAccountKey);

                // Step 6: Dao Login
                String[] loginResult = daoLogin(subAccountKey, tempCookies);
                String daoyuToken = loginResult[0];
                String updatedCookies = loginResult[1];
                logger.log(Level.INFO, "DaoYu Token: {0}", daoyuToken);
                logger.log(Level.INFO, "Updated Cookies: {0}", updatedCookies);

                // Step 7: Get Character Bind Info
                boolean bindInfoStatus = getCharacterBindInfo(daoyuToken, updatedCookies);
                logger.log(Level.INFO, "Character Bind Info Status: {0}", bindInfoStatus);

                return new String[]{daoyuToken, updatedCookies};
            } else {
                logger.log(Level.SEVERE, "Confirm failed. Unable to obtain DaoYu Token.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception occurred during the login process", e);
        }
        return new String[]{null, null};
    }

    private static String getTempCookies() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "GHome/isLogin")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting temp cookies", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String cookie = response.header("Set-Cookie");
            logger.log(Level.FINE, "Received temp cookies: {0}", cookie);
            return cookie;
        }
    }

    private static String initializeLoginFlow(String cookies) throws IOException {
        // 生成 device_id 并存储到成员变量中
        device_id = UUID.randomUUID().toString().toUpperCase().replace("-", "");
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
                .header("Cookie", cookies)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for initializing login flow", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String newCookies = response.header("Set-Cookie");
            if (newCookies != null) {
                logger.log(Level.FINE, "Updated cookies: {0}", newCookies);
                cookies = newCookies;
            }
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Initialize login flow response: {0}", jsonResponse.toJSONString());
            return jsonResponse.getJSONObject("data").getString("flowId");
        }
    }

    private static String queryAccountList(String flowId, String cookies) throws IOException {
        // 使用成员变量 device_id
        String device_manuid = generateRandomString(6);

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
                .addQueryParameter("device_id", device_id)
                .addQueryParameter("device_manuid", device_manuid)
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .addQueryParameter("flowId", flowId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookies)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for querying account list", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String newCookies = response.header("Set-Cookie");
            if (newCookies != null) {
                logger.log(Level.FINE, "Updated cookies: {0}", newCookies);
                cookies = newCookies;
            }
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Query account list response: {0}", jsonResponse.toJSONString());
            JSONArray accountList = jsonResponse.getJSONObject("data").getJSONArray("accountList");
            return accountList.getJSONObject(0).getString("accountId");
        }
    }

    private static boolean makeConfirm(String flowId, String accountId, String cookies) throws IOException {
        // 使用成员变量 device_id
        String device_manuid = generateRandomString(6);

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
                .addQueryParameter("device_id", device_id)
                .addQueryParameter("device_manuid", device_manuid)
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .addQueryParameter("flowId", flowId)
                .addQueryParameter("accountId", accountId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookies)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for making confirm", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String newCookies = response.header("Set-Cookie");
            if (newCookies != null) {
                logger.log(Level.FINE, "Updated cookies: {0}", newCookies);
                cookies = newCookies;
            }
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Make confirm response: {0}", jsonResponse.toJSONString());
            return "success".equals(jsonResponse.getString("return_message"));
        }
    }

    private static String getSubAccountKey(String flowId, String cookies) throws IOException {
        // 使用成员变量 device_id
        String device_manuid = generateRandomString(6);

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
                .addQueryParameter("device_id", device_id)
                .addQueryParameter("device_manuid", device_manuid)
                .addQueryParameter("USERSESSID", DAOGU_KEY)
                .addQueryParameter("flowId", flowId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookies)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting sub account key", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String newCookies = response.header("Set-Cookie");
            if (newCookies != null) {
                logger.log(Level.FINE, "Updated cookies: {0}", newCookies);
                cookies = newCookies;
            }
            JSONObject jsonResponse = JSONObject.parseObject(response            .body().string());
            logger.log(Level.FINE, "Get sub account key response: {0}", jsonResponse.toJSONString());
            return jsonResponse.getJSONObject("data").getString("authorization");
        }
    }

    private static String[] daoLogin(String subAccountKey, String cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "GHome/daoLogin").newBuilder()
                .addQueryParameter("authorization", subAccountKey)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookies)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for dao login", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String newCookies = response.header("Set-Cookie");
            if (newCookies != null) {
                logger.log(Level.FINE, "Updated cookies: {0}", newCookies);
                cookies = newCookies;
            }
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Dao login response: {0}", jsonResponse.toJSONString());
            String daoyuToken = jsonResponse.getJSONObject("data").getString("DaoyuToken");
            return new String[]{daoyuToken, cookies};
        }
    }

    private static boolean getCharacterBindInfo(String daoyuToken, String cookies) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL + "groupAndRole/getCharacterBindInfo").newBuilder()
                .addQueryParameter("platform", "1")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("authorization", daoyuToken)
                .header("Cookie", cookies)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.log(Level.SEVERE, "Unexpected code {0} for getting character bind info", response.code());
                throw new IOException("Unexpected code " + response.code());
            }
            String newCookies = response.header("Set-Cookie");
            if (newCookies != null) {
                logger.log(Level.FINE, "Updated cookies: {0}", newCookies);
                cookies = newCookies;
            }
            JSONObject jsonResponse = JSONObject.parseObject(response.body().string());
            logger.log(Level.FINE, "Character bind info response: {0}", jsonResponse.toJSONString());
            return jsonResponse.getIntValue("code") == 10000;
        }
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