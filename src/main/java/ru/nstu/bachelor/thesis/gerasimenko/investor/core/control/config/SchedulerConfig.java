package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "sync")
@Data
public class SchedulerConfig {

    private int poolSize = 10;
    private int terminationSeconds = 60;
    private int asyncPoolSize = 5;
    private String threadNamePrefix = "data-sync-thread-";
    private String asyncThreadNamePrefix = "async-worker-";

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(threadNamePrefix);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(terminationSeconds);
        scheduler.setErrorHandler(t -> log.error("Scheduler error: {}", t.getMessage()));
        return scheduler;
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(asyncPoolSize, r -> {
            Thread t = new Thread(r);
            t.setName(asyncThreadNamePrefix);
            t.setDaemon(true);
            return t;
        });
    }
}