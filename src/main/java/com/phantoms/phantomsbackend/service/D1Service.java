package com.phantoms.phantomsbackend.service;

import co.casterlabs.d1.result.D1Result;

import java.util.List;

public interface D1Service {
    D1Result query(String sql) throws Exception;

    D1Result[] batchQuery(List<String> sqls) throws Exception;
}