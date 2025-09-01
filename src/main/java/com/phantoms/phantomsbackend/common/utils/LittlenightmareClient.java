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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LittlenightmareClient {

    private static final Logger logger = LoggerFactory.getLogger(LittlenightmareClient.class);
    private static final String BASE_URL = "https://xivpf.littlenightmare.top/api/listings";
    private static final String SCRAPESTACK_API_URL = "http://api.scrapestack.com/scrape?access_key=ebdc1b9aa7e4f9f1c776d9ad2c90628e&url=";
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int SOCKET_TIMEOUT = 10000; // 10 seconds

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
        String scrapeStackUrl = SCRAPESTACK_API_URL + java.net.URLEncoder.encode(targetUrl, "UTF-8");

        RecruitmentResponse response = null;
        boolean success = false;

        // 尝试使用 getRandomProxy 获取的代理地址
        HttpHost proxy = ProxyUtil.getRandomProxy();
        if (proxy != null) {
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

                    // 检查返回内容是否为 HTML 页面
                    if (jsonResponse != null && jsonResponse.trim().startsWith("<")) {
                        logger.error("Invalid response received: {}", jsonResponse);
                        throw new IOException("Invalid response received: " + jsonResponse);
                    }

                    // 配置 ObjectMapper 以解析 ISO 8601 格式的日期时间
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.registerModule(CustomOffsetDateTimeDeserializer.createModule());

                    response = objectMapper.readValue(jsonResponse, RecruitmentResponse.class);
                    success = true;
                }
            } catch (IOException e) {
                logger.error("Failed to fetch recruitment listings using proxy {}: {}", proxy, e.getMessage());
            }
        }

        // 如果代理尝试失败，尝试使用 ScrapeStack
        if (!success) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(scrapeStackUrl);
                request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
                request.setHeader("Accept", "application/json");

                try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
                    String jsonResponse = EntityUtils.toString(httpResponse.getEntity());

                    // 检查返回内容是否为 HTML 页面
                    if (jsonResponse != null && jsonResponse.trim().startsWith("<")) {
                        logger.error("Invalid response received: {}", jsonResponse);
                        throw new IOException("Invalid response received: " + jsonResponse);
                    }

                    // 配置 ObjectMapper 以解析 ISO 8601 格式的日期时间
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.registerModule(CustomOffsetDateTimeDeserializer.createModule());

                    response = objectMapper.readValue(jsonResponse, RecruitmentResponse.class);
                    success = true;
                }
            } catch (IOException e) {
                logger.error("Failed to fetch recruitment listings using ScrapeStack: {}", e.getMessage());
            }
        }

        // 如果 ScrapeStack 尝试失败，尝试裸连
        if (!success) {
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

                    // 检查返回内容是否为 HTML 页面
                    if (jsonResponse != null && jsonResponse.trim().startsWith("<")) {
                        logger.error("Invalid response received: {}", jsonResponse);
                        throw new IOException("Invalid response received: " + jsonResponse);
                    }

                    // 配置 ObjectMapper 以解析 ISO 8601 格式的日期时间
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.registerModule(CustomOffsetDateTimeDeserializer.createModule());

                    response = objectMapper.readValue(jsonResponse, RecruitmentResponse.class);
                    success = true;
                }
            } catch (IOException e) {
                logger.error("Failed to fetch recruitment listings without proxy: {}", e.getMessage());
            }
        }

        if (!success) {
            throw new IOException("Failed to fetch recruitment listings after multiple attempts");
        }

        return response;
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

        while (hasMorePages) {
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

            // Check if there are more pages
            hasMorePages = response.getPagination().getPage() < response.getPagination().getTotalPages();
            page++;

            // 添加延迟以避免频率过高
            try {
                TimeUnit.SECONDS.sleep(1); // 每次请求之间延迟 1 秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return allResponses;
    }
}