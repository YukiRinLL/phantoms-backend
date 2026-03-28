package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.common.bean.ApiResponse;
import com.phantoms.phantomsbackend.common.bean.SearchResponse;
import com.phantoms.phantomsbackend.service.XIVAPIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "XIVAPI", description = "FFXIV游戏数据API接口")
public class XIVAPIController {

    private final XIVAPIService xivapiService;

    public XIVAPIController(XIVAPIService xivapiService) {
        this.xivapiService = xivapiService;
    }

    @GetMapping("/asset")
    @Operation(
            summary = "获取游戏资源",
            description = "获取FFXIV游戏资源文件（如图片等）",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "资源获取成功")
            }
    )
    public CompletableFuture<ResponseEntity<byte[]>> getAsset(
            @Parameter(description = "资源路径", required = true) @RequestParam String path,
            @Parameter(description = "资源格式（如png、jpg）", required = true) @RequestParam String format,
            @Parameter(description = "游戏版本") @RequestParam(required = false) String version) {

        return xivapiService.getAsset(path, format, version)
                .thenApply(data -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("image/" + format))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path + "." + format + "\"")
                        .body(data));
    }

    @GetMapping("/search")
    @Operation(
            summary = "搜索游戏数据",
            description = "搜索FFXIV游戏数据",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "搜索成功")
            }
    )
    public CompletableFuture<SearchResponse> search(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String query,
            @Parameter(description = "数据表名称，多个用逗号分隔") @RequestParam(required = false) String sheets,
            @Parameter(description = "分页游标") @RequestParam(required = false) String cursor,
            @Parameter(description = "返回结果数量限制") @RequestParam(required = false) Integer limit,
            @Parameter(description = "语言") @RequestParam(required = false) String language,
            @Parameter(description = "响应模式") @RequestParam(required = false) String schema,
            @Parameter(description = "返回字段") @RequestParam(required = false) String fields,
            @Parameter(description = "临时字段") @RequestParam(required = false) String transientFields) {
        UUID uuidCursor = null;
        if (cursor != null) {
            uuidCursor = UUID.fromString(cursor);
        }
        return xivapiService.search(query, sheets, uuidCursor, limit, language, schema, fields, transientFields);
    }

    @GetMapping("/sheet")
    @Operation(
            summary = "获取数据表列表",
            description = "获取所有可用的FFXIV数据表名称列表",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
            }
    )
    public CompletableFuture<ApiResponse<List<String>>> listSheets(
            @Parameter(description = "游戏版本") @RequestParam(required = false) String version) {

        return xivapiService.listSheets(version);
    }

    @GetMapping("/sheet/{sheet}")
    @Operation(
            summary = "获取数据表内容",
            description = "获取指定数据表的内容",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
            }
    )
    public CompletableFuture<ApiResponse<List<Map<String, Object>>>> getSheetData(
            @Parameter(description = "数据表名称", required = true) @PathVariable String sheet,
            @Parameter(description = "行号，多个用逗号分隔") @RequestParam(required = false) String rows,
            @Parameter(description = "返回结果数量限制") @RequestParam(required = false) Integer limit,
            @Parameter(description = "分页游标") @RequestParam(required = false) String after,
            @Parameter(description = "语言") @RequestParam(required = false) String language,
            @Parameter(description = "响应模式") @RequestParam(required = false) String schema,
            @Parameter(description = "返回字段") @RequestParam(required = false) String fields,
            @Parameter(description = "临时字段") @RequestParam(required = false) String transientFields) {

        return xivapiService.getSheetData(sheet, rows, limit, after, language, schema, fields, transientFields);
    }

    @GetMapping("/sheet/{sheet}/{row}")
    @Operation(
            summary = "获取数据表单行",
            description = "获取指定数据表中特定行的内容",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
            }
    )
    public CompletableFuture<ApiResponse<Map<String, Object>>> getSheetRow(
            @Parameter(description = "数据表名称", required = true) @PathVariable String sheet,
            @Parameter(description = "行号", required = true) @PathVariable String row,
            @Parameter(description = "语言") @RequestParam(required = false) String language,
            @Parameter(description = "响应模式") @RequestParam(required = false) String schema,
            @Parameter(description = "返回字段") @RequestParam(required = false) String fields,
            @Parameter(description = "临时字段") @RequestParam(required = false) String transientFields) {

        return xivapiService.getSheetRow(sheet, row, language, schema, fields, transientFields);
    }

    @GetMapping("/map/{territory}/{index}")
    @Operation(
            summary = "获取地图资源",
            description = "获取指定区域和索引的地图图片资源",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
            }
    )
    public CompletableFuture<ResponseEntity<byte[]>> getMapAsset(
            @Parameter(description = "区域名称", required = true) @PathVariable String territory,
            @Parameter(description = "地图索引", required = true) @PathVariable String index,
            @Parameter(description = "游戏版本") @RequestParam(required = false) String version) {

        return xivapiService.getMapAsset(territory, index, version)
                .thenApply(data -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + territory + "_" + index + ".jpg\"")
                        .body(data));
    }

    @GetMapping("/versions")
    @Operation(
            summary = "获取游戏版本列表",
            description = "获取所有可用的FFXIV游戏版本列表",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
            }
    )
    public CompletableFuture<ApiResponse<List<String>>> getVersions() {
        return xivapiService.getVersions();
    }
}
