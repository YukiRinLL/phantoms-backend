package com.phantoms.phantomsbackend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.llm.openai")
public class LLMProperties {
    private String apiUrl;
    private String apiKey;
    private String modelName;
    private double temperature;
}