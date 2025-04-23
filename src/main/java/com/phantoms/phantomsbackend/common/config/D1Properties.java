package com.phantoms.phantomsbackend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "d1")
public class D1Properties {
    private String apiUrl;
    private String accountId;
    private String databaseId;
    private String apiToken;
    private String logLevel;
}