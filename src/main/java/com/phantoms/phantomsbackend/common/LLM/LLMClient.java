package com.phantoms.phantomsbackend.common.LLM;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantoms.phantomsbackend.common.config.LLMProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Component
public class LLMClient {
    private final LLMProperties llmProperties;
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public LLMClient(LLMProperties llmProperties) {
        this.llmProperties = llmProperties;
    }

    @PostConstruct
    public void init() {
        log.info("=== LLM配置 ===");
        log.info("API URL: {}", llmProperties.getApiUrl());
        log.info("API Key: {}", llmProperties.getApiKey() != null ? llmProperties.getApiKey().substring(0, Math.min(10, llmProperties.getApiKey().length())) + "***" : "null");
        log.info("Model Name: {}", llmProperties.getModelName());
        log.info("Temperature: {}", llmProperties.getTemperature());
        log.info("===============");
    }

    public String chat(String systemPrompt, String userInput) throws Exception {
        Object[] messages = {
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userInput)
        };

        var body = Map.of(
                "model", llmProperties.getModelName(),
                "messages", messages,
                "temperature", llmProperties.getTemperature()
        );

        String json = MAPPER.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(llmProperties.getApiUrl())
                .header("Authorization", "Bearer " + llmProperties.getApiKey())
                .post(requestBody)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("LLM请求失败：" + response.code());
            }
            Map<String, Object> resMap = MAPPER.readValue(response.body().string(), Map.class);
            var choices = (Map<?, ?>) ((java.util.List<?>) resMap.get("choices")).get(0);
            var msg = (Map<?, ?>) choices.get("message");
            return (String) msg.get("content");
        }
    }
}