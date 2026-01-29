package com.phantoms.phantomsbackend.common.config;

import cn.leancloud.LCLogger;
import cn.leancloud.core.LeanCloud;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Data
@Configuration
@ConfigurationProperties(prefix = "leancloud")
public class LeanCloudConfig {

    private static final Logger logger = LoggerFactory.getLogger(LeanCloudConfig.class);

    private String appId;
    private String appKey;
    private String serverUrl;
    private String logLevel;

    @PostConstruct
    public void initializeLeanCloud() {
        logger.info("Initializing LeanCloud SDK with log level: {}", logLevel);
        try {
            // 设置日志级别
            LeanCloud.setLogLevel(LCLogger.Level.valueOf(logLevel));

            // 初始化 LeanCloud SDK
            LeanCloud.initialize(appId, appKey, serverUrl);
            logger.info("LeanCloud SDK initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize LeanCloud SDK: {}", e.getMessage(), e);
        }
    }
}