package com.phantoms.phantomsbackend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 核心线程数5，避免单线程阻塞
        scheduler.setThreadNamePrefix("FF14-Scheduler-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }

}