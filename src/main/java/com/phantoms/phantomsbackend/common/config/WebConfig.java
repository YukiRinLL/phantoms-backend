package com.phantoms.phantomsbackend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置 /api/** 路径的 CORS 支持
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // 使用 allowedOriginPatterns 允许所有来源
//              .allowedOrigins("http://localhost:3000") // 允许来自 http://localhost:3000 的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许的头部
                .allowCredentials(false); // 允许携带凭证（如 cookies）

        // 配置 /onebot/** 路径的 CORS 支持
        registry.addMapping("/onebot/**")
                .allowedOriginPatterns("*") // 使用 allowedOriginPatterns 允许所有来源
                .allowedMethods("GET", "POST") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许的头部
                .allowCredentials(false); // 允许携带凭证（如 cookies）
    }
}