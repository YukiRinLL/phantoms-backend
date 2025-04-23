package com.phantoms.phantomsbackend.common.utils;

import co.casterlabs.d1.D1;

import co.casterlabs.d1.result.D1Result;
import co.casterlabs.d1.D1Exception;
import com.phantoms.phantomsbackend.common.config.D1Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class D1Util {
    private static D1 d1Client;
    private final D1Properties d1Properties;
    private final Logger logger;

    @Autowired
    public D1Util(D1Properties d1Properties) throws D1Exception {
        this.d1Properties = d1Properties;
        this.logger = LoggerFactory.getLogger(D1Util.class);
        initialize();
    }

    private void initialize() throws D1Exception {
        try {
            d1Client = D1.builder()
                    .withApiUrl(d1Properties.getApiUrl())
                    .withAccountId(d1Properties.getAccountId())
                    .withDatabaseId(d1Properties.getDatabaseId())
                    .withApiToken(d1Properties.getApiToken())
                    .build();
            logger.info("D1 client initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize D1 client.", e);
            // 使用 Throwable 构造函数
            throw new D1Exception(e);
        }
    }

    public D1Result query(String sql, Object... params) throws D1Exception {
        if (d1Client == null) {
            throw new IllegalStateException("D1 client is not initialized.");
        }
        return d1Client.query(sql, params);
    }

    public D1Result[] batchQuery(String sql) throws D1Exception {
        if (d1Client == null) {
            throw new IllegalStateException("D1 client is not initialized.");
        }
        return d1Client.batchQuery(sql);
    }
}