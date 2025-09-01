package com.phantoms.phantomsbackend.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProxyUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);
    private static final String PROXY_API_URL = "http://www.ip3366.net/free/";
    private static List<HttpHost> proxies = new ArrayList<>();

    public static synchronized HttpHost getRandomProxy() {
        if (proxies.isEmpty()) {
            refreshProxies();
        }
        if (proxies.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return proxies.get(random.nextInt(proxies.size()));
    }

    private static void refreshProxies() {
        proxies.clear();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(PROXY_API_URL);

            // 设置请求头，模拟浏览器请求
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            request.setHeader("Accept-Language", "en-US,en;q=0.5");
            request.setHeader("Accept-Encoding", "gzip, deflate");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseText = EntityUtils.toString(entity, "UTF-8");
                    logger.info("Response text: {}", responseText);
                    if (responseText.isEmpty()) {
                        logger.warn("Response text is empty");
                        return;
                    }

                    // 使用Jsoup解析HTML
                    Document doc = Jsoup.parse(responseText);
                    Elements rows = doc.select("table tbody tr");

                    for (Element row : rows) {
                        Elements columns = row.select("td");
                        if (columns.size() >= 2) {
                            String ip = columns.get(0).text();
                            String port = columns.get(1).text();
                            proxies.add(new HttpHost(ip, Integer.parseInt(port), "http"));
                        }
                    }
                } else {
                    logger.warn("Response entity is null");
                }
            }
        } catch (IOException e) {
            logger.error("Failed to fetch proxies", e);
        }
    }
}