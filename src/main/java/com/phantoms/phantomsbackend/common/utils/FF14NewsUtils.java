package com.phantoms.phantomsbackend.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class FF14NewsUtils {

    private static final Logger logger = LoggerFactory.getLogger(FF14NewsUtils.class);

    private static final String NEWS_API_URL = "https://cqnews.web.sdo.com/api/news/newsList";
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

    public List<NewsItem> fetchNewsList() {
        List<NewsItem> newsList = new ArrayList<>();

        try {
            HttpUrl url = HttpUrl.parse(NEWS_API_URL).newBuilder()
                .addQueryParameter("gameCode", "ff")
                .addQueryParameter("CategoryCode", "8324,8325,8326,8327,5309,5310,5311,5312,5313")
                .addQueryParameter("pageIndex", "0")
                .addQueryParameter("pageSize", "5")
                .build();

            Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Referer", "https://ff.web.sdo.com/")
                .get()
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to fetch FF14 news: code={}, url={}", response.code(), url);
                    return newsList;
                }

                String responseBody = response.body().string();
                logger.debug("FF14 news response: {}", responseBody);

                JSONObject jsonResponse = JSONObject.parseObject(responseBody);
                JSONArray dataList = jsonResponse.getJSONArray("Data");

                if (dataList != null) {
                    for (int i = 0; i < dataList.size(); i++) {
                        JSONObject item = dataList.getJSONObject(i);
                        
                        String id = item.getString("Id") != null ? item.getString("Id") : "";
                        String title = item.getString("Title") != null ? item.getString("Title") : "";
                        String description = item.getString("Summary") != null ? item.getString("Summary") : "";
                        String date = item.getString("PublishDate") != null ? item.getString("PublishDate") : "";
                        String imageUrl = item.getString("HomeImagePath") != null ? item.getString("HomeImagePath").trim() : "";
                        String outLink = item.getString("OutLink") != null ? item.getString("OutLink") : "";
                        
                        String linkUrl;
                        if (!outLink.isEmpty()) {
                            linkUrl = outLink;
                        } else {
                            linkUrl = "https://ff.web.sdo.com/web8/index.html#/newstab/newscont/" + id;
                        }

                        if (date.length() >= 10) {
                            date = date.substring(0, 10);
                        }

                        newsList.add(new NewsItem(title, description, date, imageUrl, linkUrl, id));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching FF14 news", e);
        }

        return newsList;
    }
}
