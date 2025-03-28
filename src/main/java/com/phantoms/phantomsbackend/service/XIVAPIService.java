package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.common.bean.ApiResponse;
import com.phantoms.phantomsbackend.common.bean.SearchResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface XIVAPIService {
    // 获取资源文件
    CompletableFuture<byte[]> getAsset(String path, String format, String version);

    // 搜索游戏数据
    CompletableFuture<SearchResponse> search(String query, String sheets, UUID cursor, Integer limit,
                                             String language, String schema, String fields, String transientFields);
    // listSheets
    CompletableFuture<ApiResponse<List<String>>> listSheets(String version);

    // 获取表数据
    CompletableFuture<ApiResponse<List<Map<String, Object>>>> getSheetData(
            String sheet,
            String rows,
            Integer limit,
            String after,
            String language,
            String schema,
            String fields,
            String transientFields);

    // 获取单行数据
    CompletableFuture<ApiResponse<Map<String, Object>>> getSheetRow(
            String sheet,
            String row,
            String language,
            String schema,
            String fields,
            String transientFields);

    // 获取组合地图
    CompletableFuture<byte[]> getMapAsset(String territory, String index, String version);

    // 获取支持的版本列表
    CompletableFuture<ApiResponse<List<String>>> getVersions();
}