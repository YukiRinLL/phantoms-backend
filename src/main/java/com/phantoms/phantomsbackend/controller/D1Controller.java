package com.phantoms.phantomsbackend.controller;

import co.casterlabs.d1.result.D1Result;
import com.phantoms.phantomsbackend.service.D1Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/d1")
@Tag(name = "D1 Database", description = "Cloudflare D1数据库操作接口")
public class D1Controller {

    private final D1Service d1Service;

    @Autowired
    public D1Controller(D1Service d1Service) {
        this.d1Service = d1Service;
    }

    @GetMapping("/query")
    @Operation(
            summary = "执行单条SQL查询",
            description = "在Cloudflare D1数据库上执行单条SQL查询语句",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "查询失败")
            }
    )
    public ResponseEntity<D1Result> query(
            @Parameter(description = "SQL查询语句", required = true) @RequestParam String sql) {
        try {
            D1Result result = d1Service.query(sql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/batchQuery")
    @Operation(
            summary = "执行批量SQL查询",
            description = "在Cloudflare D1数据库上批量执行多条SQL查询语句",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量查询成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "批量查询失败")
            }
    )
    public ResponseEntity<List<D1Result>> batchQuery(
            @Parameter(description = "SQL查询语句列表", required = true) @RequestBody List<String> sqls) {
        try {
            D1Result[] results = d1Service.batchQuery(sqls);
            return ResponseEntity.ok(Arrays.asList(results));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
