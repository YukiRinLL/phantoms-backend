package com.phantoms.phantomsbackend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:./.env", ignoreResourceNotFound = true)
public class EnvConfig {
    // 空类，仅用于加载 .env 文件


//    private final ConfigurableEnvironment environment;
//
//    public EnvConfig(ConfigurableEnvironment environment) {
//        this.environment = environment;
//    }
//
//    @PostConstruct
//    public void loadEnv() {
//        try {
//            Dotenv dotenv = Dotenv.configure()
//                .directory("./")
//                .ignoreIfMissing()
//                .load();
//
//            Map<String, Object> envProperties = new HashMap<>();
//            dotenv.entries().forEach(entry ->
//                envProperties.put(entry.getKey(), entry.getValue())
//            );
//
//            environment.getPropertySources()
//                .addFirst(new MapPropertySource("dotenv", envProperties));
//
//        } catch (Exception e) {
//            System.err.println("Failed to load .env file: " + e.getMessage());
//        }
//    }
}