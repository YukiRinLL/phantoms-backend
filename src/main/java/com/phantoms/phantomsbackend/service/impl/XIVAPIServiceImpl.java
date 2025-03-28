package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.common.bean.ApiResponse;
import com.phantoms.phantomsbackend.common.bean.SearchResponse;
import com.phantoms.phantomsbackend.common.utils.XIVAPIClient;
import com.phantoms.phantomsbackend.service.XIVAPIService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class XIVAPIServiceImpl implements XIVAPIService {

    private final XIVAPIClient xivapiClient;

    public XIVAPIServiceImpl(XIVAPIClient xivapiClient) {
        this.xivapiClient = xivapiClient;
    }

    @Override
    public CompletableFuture<byte[]> getAsset(String path, String format, String version) {
        return xivapiClient.getAsset(path, format, version);
    }

    @Override
    public CompletableFuture<SearchResponse> search(String query, String sheets, UUID cursor, Integer limit,
                                                    String language, String schema, String fields, String transientFields) {
        return xivapiClient.search(query, sheets, cursor, limit, language, schema, fields, transientFields);
    }

    @Override
    public CompletableFuture<ApiResponse<List<String>>> listSheets(String version) {
        return xivapiClient.listSheets(version);
    }

    @Override
    public CompletableFuture<ApiResponse<List<Map<String, Object>>>> getSheetData(String sheet, String rows, Integer limit,
                                                                                  String after, String language, String schema,
                                                                                  String fields, String transientFields) {
        return xivapiClient.getSheetData(sheet, rows, limit, after, language, schema, fields, transientFields);
    }

    @Override
    public CompletableFuture<ApiResponse<Map<String, Object>>> getSheetRow(String sheet, String row, String language,
                                                                           String schema, String fields, String transientFields) {
        return xivapiClient.getSheetRow(sheet, row, language, schema, fields, transientFields);
    }

    @Override
    public CompletableFuture<byte[]> getMapAsset(String territory, String index, String version) {
        return xivapiClient.getMapAsset(territory, index, version);
    }

    @Override
    public CompletableFuture<ApiResponse<List<String>>> getVersions() {
        return xivapiClient.getVersions();
    }
}