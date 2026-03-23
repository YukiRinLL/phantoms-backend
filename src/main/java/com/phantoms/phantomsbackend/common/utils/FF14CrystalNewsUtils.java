package com.phantoms.phantomsbackend.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FF14CrystalNewsUtils {

    private static final Logger logger = LoggerFactory.getLogger(FF14CrystalNewsUtils.class);

    private static final String NEWS_API_URL = "https://apps.game.qq.com/cmc/cross";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

    public static class NewsItem {
        private String title;
        private String author;
        private String date;
        private String imageUrl;
        private String linkUrl;
        private String id;

        public NewsItem(String title, String author, String date, String imageUrl, String linkUrl, String id) {
            this.title = title;
            this.author = author;
            this.date = date;
            this.imageUrl = imageUrl;
            this.linkUrl = linkUrl;
            this.id = id;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getDate() { return date; }
        public String getImageUrl() { return imageUrl; }
        public String getLinkUrl() { return linkUrl; }
        public String getId() { return id; }
    }

    public List<NewsItem> fetchCrystalNews() {
        List<NewsItem> newsList = new ArrayList<>();

        try {
            HttpUrl url = HttpUrl.parse(NEWS_API_URL).newBuilder()
                .addQueryParameter("serviceId", "473")
                .addQueryParameter("source", "ff14")
                .addQueryParameter("tagids", "135991")
                .addQueryParameter("start", "0")
                .addQueryParameter("limit", "100")
                .build();

            Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .get()
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to fetch FF14水晶世界新闻: code={}, url={}", response.code(), url);
                    return newsList;
                }

                String responseBody = response.body().string();
                logger.debug("FF14水晶世界新闻响应: {}", responseBody);

                JSONObject jsonResponse = JSONObject.parseObject(responseBody);

                if (jsonResponse == null || jsonResponse.getIntValue("status") != 0) {
                    logger.error("FF14水晶世界新闻API返回错误: {}", jsonResponse);
                    return newsList;
                }

                JSONObject data = jsonResponse.getJSONObject("data");
                if (data == null) {
                    logger.error("FF14水晶世界新闻响应中缺少data字段");
                    return newsList;
                }

                JSONArray items = data.getJSONArray("items");
                if (items == null || items.isEmpty()) {
                    logger.warn("FF14水晶世界新闻列表为空");
                    return newsList;
                }

                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    String id = item.getString("iId") != null ? item.getString("iId") : item.getString("iNewsId");
                    String title = item.getString("sTitle") != null ? item.getString("sTitle") : "";
                    String author = item.getString("sAuthor") != null ? item.getString("sAuthor") : "";
                    String created = item.getString("sCreated") != null ? item.getString("sCreated") : "";

                    String date = created;

                    String imageUrl = "";
                    JSONArray coverList = item.getJSONArray("sCoverList");
                    if (coverList != null && !coverList.isEmpty()) {
                        for (int j = 0; j < coverList.size(); j++) {
                            JSONObject cover = coverList.getJSONObject(j);
                            String size = cover.getString("size");
                            String coverUrl = cover.getString("url");

                            if (coverUrl != null && !coverUrl.isEmpty()) {
                                // 修复URL格式，确保有正确的协议前缀
                                if (!coverUrl.startsWith("http://") && !coverUrl.startsWith("https://")) {
                                    coverUrl = "https:" + coverUrl;
                                }
                                
                                if ("502*282".equals(size)) {
                                    imageUrl = coverUrl;
                                    break;
                                } else if ("987*444".equals(size) && imageUrl.isEmpty()) {
                                    imageUrl = coverUrl;
                                } else if ("1036*608".equals(size) && imageUrl.isEmpty()) {
                                    imageUrl = coverUrl;
                                } else if (imageUrl.isEmpty()) {
                                    imageUrl = coverUrl;
                                }
                            }
                        }
                    }

                    String linkUrl = "";
                    String docId = item.getString("iDocID");
                    String redirectURL = item.getString("sRedirectURL");
                    if (docId != null && !docId.isEmpty()) {
                        linkUrl = "https://ff14m.qq.com/web202409/#/news/detail?id=" + docId;
                    } else if (redirectURL != null && !redirectURL.isEmpty()) {
                        linkUrl = redirectURL;
                    } else {
                        linkUrl = "https://ff14m.qq.com/web202409/";
                    }

                    if (id != null && !id.isEmpty() && !title.isEmpty()) {
                        newsList.add(new NewsItem(title, author, date, imageUrl, linkUrl, id));
                    }
                }

                logger.info("成功获取 {} 条FF14水晶世界新闻", newsList.size());

            }
        } catch (Exception e) {
            logger.error("获取FF14水晶世界新闻失败", e);
        }

        return newsList;
    }
}
