//package com.phantoms.phantomsbackend.common.utils;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class ProxyUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);
//    private static final String PROXY_API_URL = "http://api.89ip.cn/tqdl.html?api=1&num=60&port=&address=&isp=";
//    private static List<HttpHost> proxies = new ArrayList<>();
//
//    public static synchronized HttpHost getRandomProxy() {
//        if (proxies.isEmpty()) {
//            refreshProxies();
//        }
//        if (proxies.isEmpty()) {
//            return null;
//        }
//        Random random = new Random();
//        return proxies.get(random.nextInt(proxies.size()));
//    }
//
//    private static void refreshProxies() {
//        proxies.clear();
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpGet request = new HttpGet(PROXY_API_URL);
//
//            // 设置请求头，模拟浏览器请求
//            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
//            request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            request.setHeader("Accept-Language", "en-US,en;q=0.5");
//            request.setHeader("Accept-Encoding", "gzip, deflate");
//
//            try (CloseableHttpResponse response = httpClient.execute(request)) {
//                HttpEntity entity = response.getEntity();
//                if (entity != null) {
//                    String responseText = EntityUtils.toString(entity, "UTF-8");
//                    logger.info("Response text: {}", responseText);
//                    if (responseText.isEmpty()) {
//                        logger.warn("Response text is empty");
//                        return;
//                    }
//
//                    // 使用正则表达式匹配代理 IP 地址
//                    String ipPattern = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+\\b";
//                    Pattern pattern = Pattern.compile(ipPattern);
//                    Matcher matcher = pattern.matcher(responseText);
//
//                    while (matcher.find()) {
//                        String ipPort = matcher.group();
//                        String[] parts = ipPort.split(":");
//                        String host = parts[0];
//                        int port = Integer.parseInt(parts[1]);
//                        proxies.add(new HttpHost(host, port, "http"));
//                    }
//                } else {
//                    logger.warn("Response entity is null");
//                }
//            }
//        } catch (IOException e) {
//            logger.error("Failed to fetch proxies", e);
//        }
//    }
//}