package com.phantoms.phantomsbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.phantoms.phantomsbackend"})
@MapperScan("com.phantoms.phantomsbackend.mapper")
public class PhantomsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhantomsBackendApplication.class, args);
    }
}