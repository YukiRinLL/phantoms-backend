package com.phantoms.phantomsbackend;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
    MybatisAutoConfiguration.class  // 排除MyBatis自动配置，项目未使用Mapper
})
@EntityScan("com.phantoms.phantomsbackend.pojo.entity")
@EnableScheduling
@EnableAsync  // 启用异步支持，用于延迟初始化
//@ComponentScan(basePackages = "com.phantoms.phantomsbackend")
public class PhantomsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhantomsBackendApplication.class, args);
    }
}