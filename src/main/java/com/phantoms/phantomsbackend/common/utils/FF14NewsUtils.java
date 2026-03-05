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
            // 首先获取第一页数据，了解总页数
            JSONObject firstPageResponse = fetchPage(0, 100);
            if (firstPageResponse == null) {
                return newsList;
            }

            // 解析第一页数据
            parseNewsData(firstPageResponse, newsList);

            // 获取总页数
            int pageCount = firstPageResponse.getIntValue("PageCount");
            logger.info("Total pages: {}", pageCount);

            // 循环获取剩余页面的数据
            for (int pageIndex = 1; pageIndex < pageCount; pageIndex++) {
                logger.info("Fetching page: {}/{}", pageIndex + 1, pageCount);
                JSONObject pageResponse = fetchPage(pageIndex, 100);
                if (pageResponse != null) {
                    parseNewsData(pageResponse, newsList);
                }
                // 避免请求过快，添加短暂延迟
                Thread.sleep(100);
            }

            logger.info("Total news items fetched: {}", newsList.size());

        } catch (Exception e) {
            logger.error("Error fetching FF14 news", e);
        }

        return newsList;
    }

    private JSONObject fetchPage(int pageIndex, int pageSize) throws IOException {
        HttpUrl url = HttpUrl.parse(NEWS_API_URL).newBuilder()
            .addQueryParameter("gameCode", "ff")
            .addQueryParameter("CategoryCode", "8324,8325,8326,8327,5309,5310,5311,5312,5313")
            .addQueryParameter("pageIndex", String.valueOf(pageIndex))
            .addQueryParameter("pageSize", String.valueOf(pageSize))
            .build();

        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Referer", "https://ff.web.sdo.com/")
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to fetch FF14 news page {}: code={}, url={}", pageIndex, response.code(), url);
                return null;
            }

            String responseBody = response.body().string();
            logger.debug("FF14 news page {} response: {}", pageIndex, responseBody);

            return JSONObject.parseObject(responseBody);
        }
    }

    private void parseNewsData(JSONObject jsonResponse, List<NewsItem> newsList) {
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
}
