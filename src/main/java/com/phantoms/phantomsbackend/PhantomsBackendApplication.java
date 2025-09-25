package com.phantoms.phantomsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.phantoms.phantomsbackend.pojo.entity")
@EnableScheduling
//@ComponentScan(basePackages = "com.phantoms.phantomsbackend")
public class PhantomsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhantomsBackendApplication.class, args);
    }
}