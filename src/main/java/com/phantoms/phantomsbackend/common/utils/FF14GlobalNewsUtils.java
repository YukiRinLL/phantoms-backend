package com.phantoms.phantomsbackend.common.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class FF14GlobalNewsUtils {

    private static final Logger logger = LoggerFactory.getLogger(FF14GlobalNewsUtils.class);

    private static final String NEWS_RSS_URL = "https://jp.finalfantasyxiv.com/lodestone/news/news.xml";
    private static final String TOPICS_RSS_URL = "https://jp.finalfantasyxiv.com/lodestone/news/topics.xml";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

    public static class NewsItem {
        private String title;
        private String description;
        private String date;
        private String imageUrl;
        private String linkUrl;
        private String id;

        public NewsItem(String title, String description, String date, String imageUrl, String linkUrl, String id) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.imageUrl = imageUrl;
            this.linkUrl = linkUrl;
            this.id = id;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDate() { return date; }
        public String getImageUrl() { return imageUrl; }
        public String getLinkUrl() { return linkUrl; }
        public String getId() { return id; }
    }

    public List<NewsItem> fetchGlobalNews() {
        List<NewsItem> newsList = new ArrayList<>();
        
        try {
            // 获取新闻RSS
            newsList.addAll(fetchRSS(NEWS_RSS_URL));
            // 获取活动RSS
            newsList.addAll(fetchRSS(TOPICS_RSS_URL));
        } catch (Exception e) {
            logger.error("Error fetching global news", e);
        }
        
        return newsList;
    }

    private List<NewsItem> fetchRSS(String rssUrl) throws Exception {
        List<NewsItem> newsList = new ArrayList<>();
        
        Request request = new Request.Builder()
            .url(rssUrl)
            .header("User-Agent", USER_AGENT)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to fetch RSS: code={}, url={}", response.code(), rssUrl);
                return newsList;
            }

            try (InputStream inputStream = response.body().byteStream()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputStream);
                doc.getDocumentElement().normalize();

                NodeList itemList = doc.getElementsByTagName("entry");
                
                for (int i = 0; i < itemList.getLength(); i++) {
                    Node itemNode = itemList.item(i);
                    
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element itemElement = (Element) itemNode;
                        
                        String title = getTagValue("title", itemElement);
                        String summary = getTagValue("summary", itemElement);
                        String published = getTagValue("published", itemElement);
                        String id = getTagValue("id", itemElement);
                        
                        // 获取链接
                        String linkUrl = "";
                        NodeList linkList = itemElement.getElementsByTagName("link");
                        for (int j = 0; j < linkList.getLength(); j++) {
                            Element linkElement = (Element) linkList.item(j);
                            if (linkElement.getAttribute("rel").equals("alternate")) {
                                linkUrl = linkElement.getAttribute("href");
                                break;
                            }
                        }
                        
                        // 获取图片
                        String imageUrl = "";
                        NodeList enclosureList = itemElement.getElementsByTagName("link");
                        for (int j = 0; j < enclosureList.getLength(); j++) {
                            Element enclosureElement = (Element) enclosureList.item(j);
                            if (enclosureElement.getAttribute("rel").equals("enclosure")) {
                                imageUrl = enclosureElement.getAttribute("href");
                                break;
                            }
                        }
                        
                        // 处理日期
                        String date = published;
                        if (date != null && date.length() >= 10) {
                            date = date.substring(0, 10);
                        }
                        
                        // 处理描述
                        String description = summary != null ? summary.replaceAll("<[^>]*>", "").trim() : "";
                        
                        if (title != null && !title.isEmpty()) {
                            newsList.add(new NewsItem(title, description, date, imageUrl, linkUrl, id));
                        }
                    }
                }
            }
        }
        
        return newsList;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }
}
