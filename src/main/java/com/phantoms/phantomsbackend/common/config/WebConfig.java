package com.phantoms.phantomsbackend.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminKeyInterceptor adminKeyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 房屋监控配置与账号管理接口需要管理员密码
        registry.addInterceptor(adminKeyInterceptor)
                .addPathPatterns(
                        "/api/housing/notify/**",
                        "/api/ffxiv/signin/accounts/**",
                        "/api/ffxiv/signin/manual-signin/**",
                        "/api/ffxiv/signin/manual-claim-rewards/**"
                );
    }

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