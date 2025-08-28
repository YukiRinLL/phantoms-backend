package com.phantoms.phantomsbackend.common.utils;

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
    private static final String PROXY_API_URL = "http://api.89ip.cn/tqdl.html?api=1&num=60&port=&address=&isp=";
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
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getEntity() != null) {
                    String responseText = EntityUtils.toString(response.getEntity(), "UTF-8");
//TODO bug: 这里responseText始终是空的
                    if (responseText.isEmpty()) {
                        logger.warn("Response text is empty");
                        return;
                    }

                    // 使用 Jsoup 解析 HTML 内容
                    Document doc = Jsoup.parse(responseText);
                    Elements elements = doc.body().children();

                    for (Element element : elements) {
                        if (element.text().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+")) {
                            String[] parts = element.text().split(":");
                            String host = parts[0];
                            int port = Integer.parseInt(parts[1]);
                            proxies.add(new HttpHost(host, port, "http"));
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