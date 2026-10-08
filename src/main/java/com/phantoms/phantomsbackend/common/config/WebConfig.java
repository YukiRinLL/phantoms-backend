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
    private AdminApiAccess adminApiAccess;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 房屋监控配置：所有读写操作都需要管理员密码
        registry.addInterceptor(new AdminKeyInterceptor(adminApiAccess, false))
                .addPathPatterns("/api/housing/notify/**");

        // 账号管理：列表/详情等只读操作公开，修改/删除/启停/设默认等写操作需要管理员密码
        registry.addInterceptor(new AdminKeyInterceptor(adminApiAccess, true))
                .addPathPatterns("/api/ffxiv/signin/accounts/**");
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