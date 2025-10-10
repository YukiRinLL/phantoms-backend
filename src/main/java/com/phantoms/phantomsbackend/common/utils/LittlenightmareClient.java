package com.phantoms.phantomsbackend.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.phantoms.phantomsbackend.common.serializer.CustomOffsetDateTimeDeserializer;
import com.phantoms.phantomsbackend.pojo.entity.RecruitmentResponse;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class LittlenightmareClient {

    private static final Logger logger = LoggerFactory.getLogger(LittlenightmareClient.class);
    private static final String BASE_URL = "https://xivpf.littlenightmare.top/api/listings";
    private static final String ALLORIGINS_RAW_URL = "https://api.allorigins.win/raw?url=";
    private static final String ALLORIGINS_GET_URL = "https://api.allorigins.win/get?url=";
    private static final String CORS_ANYWHERE_URL = "https://cors-anywhere.herokuapp.com/";
    private static final String SCRAPESTACK_API_URL = "http://api.scrapestack.com/scrape";
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int SOCKET_TIMEOUT = 10000; // 10 seconds

    // 添加重试和延迟配置
    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 2000;
    private static final long MAX_DELAY_MS = 10000;

    @Value("${scrapestack.access-key}")
    private static String scrapestackAccessKey;

    public static RecruitmentResponse fetchRecruitmentListings(
            Integer page,
            Integer perPage,
            String category,
            String world,
            String search,
            String datacenter,
            List<Integer> jobs,
            List<Integer> duties
    ) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);

        if (page != null) {
            urlBuilder.append("?page=").append(page);
        }

        if (perPage != null) {
            if (urlBuilder.toString().contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("per_page=").append(perPage);
        }

        if (category != null && !category.isEmpty()) {
            if (urlBuilder.toString().contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("category=").append(category);
        }

        if (world != null && !world.isEmpty()) {
            if (urlBuilder.toString().contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("world=").append(world);
        }

        if (search != null && !search.isEmpty()) {
            if (urlBuilder.toString().contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("search=").append(search);
        }

        if (datacenter != null && !datacenter.isEmpty()) {
            if (urlBuilder.toString().contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("datacenter=").append(datacenter);
        }

        if (jobs != null && !jobs.isEmpty()) {
            for (Integer job : jobs) {
                if (urlBuilder.toString().contains("?")) {
                    urlBuilder.append("&");
                } else {
                    urlBuilder.append("?");
                }
                urlBuilder.append("jobs[]=").append(job);
            }
        }

        if (duties != null && !duties.isEmpty()) {
            for (Integer duty : duties) {
                if (urlBuilder.toString().contains("?")) {
                    urlBuilder.append("&");
                } else {
                    urlBuilder.append("?");
                }
                urlBuilder.append("duty[]=").append(duty);
            }
        }

        String targetUrl = urlBuilder.toString();
        String encodedTargetUrl = java.net.URLEncoder.encode(targetUrl, "UTF-8");

        // 构建代理URL
        String allOriginsRawUrl = ALLORIGINS_RAW_URL + encodedTargetUrl;
        String allOriginsGetUrl = ALLORIGINS_GET_URL + encodedTargetUrl;
        String corsAnywhereUrl = CORS_ANYWHERE_URL + targetUrl;
        String scrapeStackUrl = SCRAPESTACK_API_URL + "?access_key=" + scrapestackAccessKey + "&url=" + encodedTargetUrl;

        RecruitmentResponse response = null;
        boolean success = false;

        // 尝试直连
        try {
            response = tryDirectConnection(targetUrl);
            if (response != null) {
                success = true;
                logger.info("Successfully fetched data via direct connection");
            }
        } catch (IOException e) {
            logger.info("Failed to fetch recruitment listings without proxy.");
        }

        // 如果直连失败，尝试使用 AllOrigins Raw（带重试）
        if (!success) {
            logger.info("Trying AllOrigins Raw...");
            for (int retry = 0; retry < MAX_RETRIES; retry++) {
                if (success) break;

                try {
                    response = tryAllOriginsRaw(allOriginsRawUrl);
                    if (response != null) {
                        success = true;
                        logger.info("Successfully fetched data via AllOrigins Raw (attempt {})", retry + 1);
                        break;
                    }
                } catch (IOException e) {
                    logger.warn("Attempt {} failed with AllOrigins Raw: {}", retry + 1, e.getMessage());
                    if (retry < MAX_RETRIES - 1) {
                        // 指数退避延迟
                        long delay = Math.min(BASE_DELAY_MS * (long) Math.pow(2, retry), MAX_DELAY_MS);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Request interrupted", ie);
                        }
                    }
                }
            }
        }

        // 如果 AllOrigins Raw 失败，尝试使用 AllOrigins Get（带重试）
        if (!success) {
            logger.info("Trying AllOrigins Get...");
            for (int retry = 0; retry < MAX_RETRIES; retry++) {
                if (success) break;

                try {
                    response = tryAllOriginsGet(allOriginsGetUrl);
                    if (response != null) {
                        success = true;
                        logger.info("Successfully fetched data via AllOrigins Get (attempt {})", retry + 1);
                        break;
                    }
                } catch (IOException e) {
                    logger.warn("Attempt {} failed with AllOrigins Get: {}", retry + 1, e.getMessage());
                    if (retry < MAX_RETRIES - 1) {
                        long delay = Math.min(BASE_DELAY_MS * (long) Math.pow(2, retry), MAX_DELAY_MS);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Request interrupted", ie);
                        }
                    }
                }
            }
        }

        // 如果 AllOrigins 失败，尝试使用 CORS Anywhere（带重试）
        if (!success) {
            logger.info("Trying CORS Anywhere...");
            for (int retry = 0; retry < MAX_RETRIES; retry++) {
                if (success) break;

                try {
                    response = tryCorsAnywhere(corsAnywhereUrl);
                    if (response != null) {
                        success = true;
                        logger.info("Successfully fetched data via CORS Anywhere (attempt {})", retry + 1);
                        break;
                    }
                } catch (IOException e) {
                    logger.warn("Attempt {} failed with CORS Anywhere: {}", retry + 1, e.getMessage());
                    if (retry < MAX_RETRIES - 1) {
                        long delay = Math.min(BASE_DELAY_MS * (long) Math.pow(2, retry), MAX_DELAY_MS);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Request interrupted", ie);
                        }
                    }
                }
            }
        }

        // 如果 CORS Anywhere 失败，尝试使用代理
        if (!success) {
            logger.info("Trying proxy servers...");
            for (int attempt = 0; attempt < 3; attempt++) {
                HttpHost proxy = ProxyUtil.getRandomProxy();
                if (proxy == null) {
                    logger.warn("No proxy available, skipping proxy attempt");
                    continue;
                }
                try (CloseableHttpClient httpClient = HttpClients.custom()
                        .setProxy(proxy)
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(CONNECT_TIMEOUT)
                                .setSocketTimeout(SOCKET_TIMEOUT)
                                .build())
                        .build()) {
                    HttpGet request = new HttpGet(targetUrl);
                    request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
                    request.setHeader("Accept", "application/json");

                    try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                        String jsonResponse = EntityUtils.toString(httpResponse.getEntity());

                        if (jsonResponse != null && !jsonResponse.trim().startsWith("<")) {
                            response = parseJsonResponse(jsonResponse);
                            success = true;
                            logger.info("Successfully fetched data via proxy: {}", proxy);
                            break;
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to fetch recruitment listings using proxy {}: {}", proxy, e.getMessage());
                }

                // 代理重试延迟
                if (!success && attempt < 2) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // 如果代理尝试失败，尝试使用 ScrapeStack
        if (!success) {
            logger.info("Trying ScrapeStack...");
            for (int retry = 0; retry < MAX_RETRIES; retry++) {
                if (success) break;

                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(scrapeStackUrl);
                    request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
                    request.setHeader("Accept", "application/json");

                    try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                        String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
                        logger.info("ScrapeStack response status: {}", httpResponse.getStatusLine().getStatusCode());

                        if (jsonResponse != null && !jsonResponse.trim().startsWith("<")) {
                            response = parseJsonResponse(jsonResponse);
                            success = true;
                            logger.info("Successfully fetched data via ScrapeStack (attempt {})", retry + 1);
                            break;
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to fetch recruitment listings using ScrapeStack (attempt {}): {}", retry + 1, e.getMessage());
                    if (retry < MAX_RETRIES - 1) {
                        try {
                            Thread.sleep(BASE_DELAY_MS * (long) Math.pow(2, retry));
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Request interrupted", ie);
                        }
                    }
                }
            }
        }

        if (!success) {
            throw new IOException("Failed to fetch recruitment listings after multiple attempts");
        }

        return response;
    }

    // 提取各个连接方法为独立函数
    private static RecruitmentResponse tryDirectConnection(String targetUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(CONNECT_TIMEOUT)
                        .setSocketTimeout(SOCKET_TIMEOUT)
                        .build())
                .build()) {
            HttpGet request = new HttpGet(targetUrl);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(httpResponse.getEntity());

                if (jsonResponse != null && jsonResponse.trim().startsWith("<")) {
                    throw new IOException("Invalid HTML response received");
                }

                return parseJsonResponse(jsonResponse);
            }
        }
    }

    private static RecruitmentResponse tryAllOriginsRaw(String allOriginsRawUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(allOriginsRawUrl);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
                logger.info("AllOrigins Raw response status: {}", httpResponse.getStatusLine().getStatusCode());

                if (jsonResponse != null && !jsonResponse.trim().startsWith("<")) {
                    return parseJsonResponse(jsonResponse);
                }
            }
        }
        return null;
    }

    private static RecruitmentResponse tryAllOriginsGet(String allOriginsGetUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(allOriginsGetUrl);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
                logger.info("AllOrigins Get response status: {}", httpResponse.getStatusLine().getStatusCode());

                if (jsonResponse != null && !jsonResponse.trim().startsWith("<")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    var wrapper = objectMapper.readTree(jsonResponse);
                    if (wrapper.has("contents")) {
                        String contents = wrapper.get("contents").asText();
                        return parseJsonResponse(contents);
                    }
                }
            }
        }
        return null;
    }

    private static RecruitmentResponse tryCorsAnywhere(String corsAnywhereUrl) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(corsAnywhereUrl);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
                logger.info("CORS Anywhere response status: {}", httpResponse.getStatusLine().getStatusCode());

                if (jsonResponse != null && !jsonResponse.trim().startsWith("<")) {
                    return parseJsonResponse(jsonResponse);
                }
            }
        }
        return null;
    }

    /**
     * 解析JSON响应的辅助方法
     */
    private static RecruitmentResponse parseJsonResponse(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(CustomOffsetDateTimeDeserializer.createModule());
        return objectMapper.readValue(jsonResponse, RecruitmentResponse.class);
    }

    public static List<RecruitmentResponse> fetchAllRecruitmentListings(
            Integer perPage,
            String category,
            String world,
            String search,
            String datacenter,
            List<Integer> jobs,
            List<Integer> duties
    ) throws IOException {
        List<RecruitmentResponse> allResponses = new ArrayList<>();
        int page = 1;
        boolean hasMorePages = true;
        int consecutiveFailures = 0;
        final int MAX_CONSECUTIVE_FAILURES = 2;

        while (hasMorePages && consecutiveFailures < MAX_CONSECUTIVE_FAILURES) {
            try {
                RecruitmentResponse response = fetchRecruitmentListings(
                        page,
                        perPage,
                        category,
                        world,
                        search,
                        datacenter,
                        jobs,
                        duties
                );

                allResponses.add(response);
                consecutiveFailures = 0; // 重置连续失败计数

                // Check if there are more pages
                hasMorePages = response.getPagination().getPage() < response.getPagination().getTotalPages();
                page++;

                // 增加延迟时间，避免频率过高
                try {
                    TimeUnit.SECONDS.sleep(3); // 从1秒增加到3秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

            } catch (IOException e) {
                consecutiveFailures++;
                logger.error("Failed to fetch page {}, consecutive failures: {}", page, consecutiveFailures);

                if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                    logger.error("Too many consecutive failures, stopping pagination");
                    break;
                }

                // 失败时增加更长的延迟
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return allResponses;
    }
}