package com.phantoms.phantomsbackend.common.config;

import cn.leancloud.LCLogger;
import cn.leancloud.core.LeanCloud;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "leancloud")
public class LeanCloudConfig {
    private String appId;
    private String appKey;
    private String serverUrl;
    private String logLevel;

    @Bean
    public void initializeLeanCloud() {
        // 设置日志级别
        LeanCloud.setLogLevel(LCLogger.Level.valueOf(logLevel));

        // 初始化 LeanCloud SDK
        LeanCloud.initialize(appId, appKey, serverUrl);
    }
}