package com.dispatcher.dispatcher.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger logger =  LogManager.getLogger();

    private final int max_dispatcher = 100;

    @Bean
    public ExecutorService Executor() {

        logger.info("Creating ThreadPool");

        final ExecutorService executorService = Executors.newFixedThreadPool(max_dispatcher);

        return executorService;
    }
}
