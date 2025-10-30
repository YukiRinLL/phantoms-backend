package com.phantoms.phantomsbackend.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.phantoms.phantomsbackend.common.serializer.CustomOffsetDateTimeDeserializer;
import com.phantoms.phantomsbackend.pojo.entity.HousingSale;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CyouClient {

    private static final Logger logger = LoggerFactory.getLogger(CyouClient.class);
    private static final String BASE_URL = "https://xivpf.littlenightmare.top/api/listings";
    private static final String ALLORIGINS_RAW_URL = "https://api.allorigins.win/raw?url=";
    private static final String ALLORIGINS_GET_URL = "https://api.allorigins.win/get?url=";
    private static final String CORS_ANYWHERE_URL = "https://cors-anywhere.herokuapp.com/";
    private static final String SCRAPESTACK_API_URL = "http://api.scrapestack.com/scrape";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;

    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 2000;
    private static final long MAX_DELAY_MS = 10000;

    @Value("${scrapestack.access-key}")
    private static String scrapestackAccessKey;

    /**
     * 执行HTTP GET请求的通用方法
     */
    private static String httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    /**
     * 执行HTTP GET请求的通用方法（带代理）
     */
    private static String httpGet(String url, HttpHost proxy) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient(proxy)) {
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            request.setHeader("Accept", "application/json");
            request.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode != 200) {
                    throw new IOException("HTTP " + statusCode + " - " + responseBody);
                }

                return responseBody;
            }
        }
    }

    /**
     * 创建HTTP客户端
     */
    private static CloseableHttpClient createHttpClient(HttpHost proxy) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT);

        if (proxy != null) {
            requestConfigBuilder.setProxy(proxy);
        }

        return HttpClients.custom()
            .setDefaultRequestConfig(requestConfigBuilder.build())
            .build();
    }

    /**
     * 获取指定服务器的房屋销售列表（带重试机制）
     */
    public static List<HousingSale> fetchHousingSales(String serverId) {
        String url = "https://api.littlenightmare.top/api/sales?server=" + serverId;

        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                String response = httpGetWithRetry(url, retry);

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                List<HousingSale> result = mapper.readValue(response,
                    mapper.getTypeFactory().constructCollectionType(List.class, HousingSale.class));

                logger.info("成功获取服务器 {} 的房屋数据: {} 条记录", serverId, result.size());
                return result;

            } catch (Exception e) {
                logger.error("获取房屋销售数据失败 (尝试 {}): server={}, error={}",
                    retry + 1, serverId, e.getMessage());

                if (retry == MAX_RETRIES - 1) {
                    logger.error("服务器 {} 的所有重试尝试都失败了", serverId);
                    return Collections.emptyList();
                }

                try {
                    long delay = Math.min(BASE_DELAY_MS * (long) Math.pow(2, retry), MAX_DELAY_MS);
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * 带重试的HTTP GET请求
     */
    private static String httpGetWithRetry(String url, int retryCount) throws IOException {
        try {
            return httpGet(url);
        } catch (IOException e) {
            logger.warn("直连失败 (尝试 {}): {}", retryCount + 1, e.getMessage());
        }

        HttpHost proxy = ProxyUtil.getRandomProxy();
        if (proxy != null) {
            try {
                logger.info("尝试使用代理: {}", proxy);
                return httpGet(url, proxy);
            } catch (IOException e) {
                logger.warn("代理请求失败 (尝试 {}): {}", retryCount + 1, e.getMessage());
            }
        }

        try {
            String corsUrl = CORS_ANYWHERE_URL + url;
            logger.info("尝试CORS代理");
            return httpGet(corsUrl);
        } catch (IOException e) {
            logger.warn("CORS代理失败 (尝试 {}): {}", retryCount + 1, e.getMessage());
        }

        throw new IOException("所有HTTP请求方式都失败了");
    }
}