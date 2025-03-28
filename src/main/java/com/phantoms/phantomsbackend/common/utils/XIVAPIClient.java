package com.phantoms.phantomsbackend.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantoms.phantomsbackend.common.bean.ApiResponse;
import com.phantoms.phantomsbackend.common.bean.SearchResponse;
import com.phantoms.phantomsbackend.common.config.XIVAPIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class XIVAPIClient {
    private final Logger log = LoggerFactory.getLogger(XIVAPIClient.class);

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public XIVAPIClient(XIVAPIConfig xivapiConfig) {
        this.baseUrl = xivapiConfig.getBaseUrl();
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        // 如果需要处理 Java 8 时间类型
        // this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 1. 资源文件相关 API

    /**
     * 获取游戏资源文件
     * @param path 资源路径，如 "ui/icon/051000/051474_hr1.tex"
     * @param format 格式，如 "png"
     * @param version 游戏版本（可选）
     * @return 资源的字节数组
     */
    public CompletableFuture<byte[]> getAsset(String path, String format, String version) {
        String url = baseUrl + "/asset?path=" + encodeValue(path) + "&format=" + encodeValue(format);
        if (version != null) {
            url += "&version=" + encodeValue(version);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body);
    }

    /**
     * 获取组合地图
     * @param territory 地图区域代码，如 "s1d1"
     * @param index 地图索引，如 "00"
     * @param version 游戏版本（可选）
     * @return 地图图片的字节数组
     */
    public CompletableFuture<byte[]> getMapAsset(String territory, String index, String version) {
        String url = baseUrl + "/asset/map/" + encodeValue(territory) + "/" + encodeValue(index);
        if (version != null) {
            url += "?version=" + encodeValue(version);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body);
    }

    // 2. 数据搜索 API

    /**
     * 执行搜索查询
     * @param query 搜索查询字符串
     * @param sheets 要搜索的表名列表（逗号分隔）
     * @param cursor 分页游标
     * @param limit 返回的最大行数
     * @param language 语言代码
     * @param schema 数据模式
     * @param fields 要返回的字段
     * @param transientFields 要返回的临时行字段
     * @return 搜索响应
     */
    public CompletableFuture<SearchResponse> search(
            String query,
            String sheets,
            UUID cursor,
            Integer limit,
            String language,
            String schema,
            String fields,
            String transientFields) {

        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/search?");

        if (cursor != null) {
            urlBuilder.append("cursor=").append(encodeValue(cursor.toString())).append("&");
        } else {
            if (query != null) {
                urlBuilder.append("query=").append(encodeValue(query)).append("&");
            }
            if (sheets != null) {
                urlBuilder.append("sheets=").append(encodeValue(sheets)).append("&");
            }
        }

        if (limit != null) {
            urlBuilder.append("limit=").append(limit).append("&");
        }
        if (language != null) {
            urlBuilder.append("language=").append(encodeValue(language)).append("&");
        }
        if (schema != null) {
            urlBuilder.append("schema=").append(encodeValue(schema)).append("&");
        }
        if (fields != null) {
            urlBuilder.append("fields=").append(encodeValue(fields)).append("&");
        }
        if (transientFields != null) {
            urlBuilder.append("transient=").append(encodeValue(transientFields)).append("&");
        }

        // 移除最后一个 &
        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), SearchResponse.class);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse search response: " + e.getMessage(), e);
                        }
                    } else {
                        try {
                            ApiResponse<?> errorResponse = objectMapper.readValue(response.body(), ApiResponse.class);
                            throw new RuntimeException("API request failed: " + errorResponse.getMessage());
                        } catch (IOException e) {
                            throw new RuntimeException("API request failed with status code: " + response.statusCode() +
                                    " and body: " + response.body());
                        }
                    }
                });
    }

    // 3. 数据表操作 API

    /**
     * 列出所有表
     * @param version 游戏版本（可选）
     * @return 表名列表
     */
    public CompletableFuture<ApiResponse<List<String>>> listSheets(String version) {
        String url = baseUrl + "/sheet";
        if (version != null) {
            url += "?version=" + encodeValue(version);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, new TypeReference<ApiResponse<List<String>>>() {}));
    }

    /**
     * 读取表中的多行数据
     * @param sheet 表名
     * @param rows 行号列表（逗号分隔）
     * @param limit 返回的最大行数
     * @param after 从指定行之后开始获取
     * @param language 语言代码
     * @param schema 数据模式
     * @param fields 要返回的字段
     * @param transientFields 要返回的临时行字段
     * @return 表数据响应
     */
    public CompletableFuture<ApiResponse<List<Map<String, Object>>>> getSheetData(
            String sheet,
            String rows,
            Integer limit,
            String after,
            String language,
            String schema,
            String fields,
            String transientFields) {

        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/sheet/" + encodeValue(sheet) + "?");

        if (rows != null) {
            urlBuilder.append("rows=").append(encodeValue(rows)).append("&");
        }
        if (limit != null) {
            urlBuilder.append("limit=").append(limit).append("&");
        }
        if (after != null) {
            urlBuilder.append("after=").append(encodeValue(after)).append("&");
        }
        if (language != null) {
            urlBuilder.append("language=").append(encodeValue(language)).append("&");
        }
        if (schema != null) {
            urlBuilder.append("schema=").append(encodeValue(schema)).append("&");
        }
        if (fields != null) {
            urlBuilder.append("fields=").append(encodeValue(fields)).append("&");
        }
        if (transientFields != null) {
            urlBuilder.append("transient=").append(encodeValue(transientFields)).append("&");
        }

        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, new TypeReference<ApiResponse<List<Map<String, Object>>>>() {}));
    }

    /**
     * 读取表中的单行数据
     * @param sheet 表名
     * @param row 行号
     * @param language 语言代码
     * @param schema 数据模式
     * @param fields 要返回的字段
     * @param transientFields 要返回的临时行字段
     * @return 行数据响应
     */
    public CompletableFuture<ApiResponse<Map<String, Object>>> getSheetRow(
            String sheet,
            String row,
            String language,
            String schema,
            String fields,
            String transientFields) {

        StringBuilder urlBuilder = new StringBuilder(baseUrl + "/sheet/" + encodeValue(sheet) + "/" + encodeValue(row) + "?");

        if (language != null) {
            urlBuilder.append("language=").append(encodeValue(language)).append("&");
        }
        if (schema != null) {
            urlBuilder.append("schema=").append(encodeValue(schema)).append("&");
        }
        if (fields != null) {
            urlBuilder.append("fields=").append(encodeValue(fields)).append("&");
        }
        if (transientFields != null) {
            urlBuilder.append("transient=").append(encodeValue(transientFields)).append("&");
        }

        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, new TypeReference<ApiResponse<Map<String, Object>>>() {}));
    }

    // 4. 版本信息 API

    /**
     * 获取支持的版本列表
     * @return 版本列表响应
     */
    public CompletableFuture<ApiResponse<List<String>>> getVersions() {
        String url = baseUrl + "/version";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, new TypeReference<ApiResponse<List<String>>>() {}));
    }

    // 辅助方法

    private <T> T parseResponse(HttpResponse<String> response, TypeReference<T> typeReference) {
        if (response.statusCode() == 200) {
            try {
                log.info("Response body: {}", response.body());
                return objectMapper.readValue(response.body(), typeReference);
            } catch (IOException e) {
                log.error("Failed to parse response: {}", response.body(), e);
                throw new RuntimeException("Failed to parse response", e);
            }
        } else {
            try {
                ApiResponse<?> errorResponse = objectMapper.readValue(response.body(), ApiResponse.class);
                log.error("API request failed with status code: {} and body: {}", response.statusCode(), response.body());
                throw new RuntimeException("API request failed: " + errorResponse.getMessage());
            } catch (IOException e) {
                log.error("API request failed with status code: {} and body: {}", response.statusCode(), response.body());
                throw new RuntimeException("API request failed with status code: " + response.statusCode());
            }
        }
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}