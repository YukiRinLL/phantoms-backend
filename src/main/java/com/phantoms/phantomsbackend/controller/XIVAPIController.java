package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.bean.ApiResponse;
import com.phantoms.phantomsbackend.common.bean.SearchResponse;
import com.phantoms.phantomsbackend.service.XIVAPIService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/xivapi")
public class XIVAPIController {

    private final XIVAPIService xivapiService;

    public XIVAPIController(XIVAPIService xivapiService) {
        this.xivapiService = xivapiService;
    }

    @GetMapping("/asset")
    public CompletableFuture<ResponseEntity<byte[]>> getAsset(
            @RequestParam String path,
            @RequestParam String format,
            @RequestParam(required = false) String version) {

        return xivapiService.getAsset(path, format, version)
                .thenApply(data -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("image/" + format))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path + "." + format + "\"")
                        .body(data));
    }

    @GetMapping("/search")
    public CompletableFuture<SearchResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String sheets,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String schema,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String transientFields) {
        UUID uuidCursor = null;
        if (cursor != null) {
            uuidCursor = UUID.fromString(cursor);
        }
        return xivapiService.search(query, sheets, uuidCursor, limit, language, schema, fields, transientFields);
    }

    @GetMapping("/sheet")
    public CompletableFuture<ApiResponse<List<String>>> listSheets(
            @RequestParam(required = false) String version) {

        return xivapiService.listSheets(version);
    }

    @GetMapping("/sheet/{sheet}")
    public CompletableFuture<ApiResponse<List<Map<String, Object>>>> getSheetData(
            @PathVariable String sheet,
            @RequestParam(required = false) String rows,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String after,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String schema,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String transientFields) {

        return xivapiService.getSheetData(sheet, rows, limit, after, language, schema, fields, transientFields);
    }

    @GetMapping("/sheet/{sheet}/{row}")
    public CompletableFuture<ApiResponse<Map<String, Object>>> getSheetRow(
            @PathVariable String sheet,
            @PathVariable String row,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String schema,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String transientFields) {

        return xivapiService.getSheetRow(sheet, row, language, schema, fields, transientFields);
    }

    @GetMapping("/map/{territory}/{index}")
    public CompletableFuture<ResponseEntity<byte[]>> getMapAsset(
            @PathVariable String territory,
            @PathVariable String index,
            @RequestParam(required = false) String version) {

        return xivapiService.getMapAsset(territory, index, version)
                .thenApply(data -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + territory + "_" + index + ".jpg\"")
                        .body(data));
    }

    @GetMapping("/versions")
    public CompletableFuture<ApiResponse<List<String>>> getVersions() {
        return xivapiService.getVersions();
    }
}