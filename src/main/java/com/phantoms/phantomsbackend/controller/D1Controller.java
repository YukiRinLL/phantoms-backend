package com.phantoms.phantomsbackend.controller;

import co.casterlabs.d1.result.D1Result;
import com.phantoms.phantomsbackend.service.D1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/d1")
public class D1Controller {

    private final D1Service d1Service;

    @Autowired
    public D1Controller(D1Service d1Service) {
        this.d1Service = d1Service;
    }

    @GetMapping("/query")
    public ResponseEntity<D1Result> query(@RequestParam String sql) {
        try {
            D1Result result = d1Service.query(sql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/batchQuery")
    public ResponseEntity<List<D1Result>> batchQuery(@RequestBody List<String> sqls) {
        try {
            D1Result[] results = d1Service.batchQuery(sqls);
            return ResponseEntity.ok(Arrays.asList(results));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}