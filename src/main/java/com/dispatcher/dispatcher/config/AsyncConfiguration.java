package com.dispatcher.dispatcher.config;


import com.dispatcher.dispatcher.exception.MyAsyncUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    private static final Logger logger =  LogManager.getLogger();

    private final int max_dispatcher = 100;

    @Bean
    public ExecutorService Executor() {

        logger.info("Creating ThreadPool");

        final ExecutorService executorService = Executors.newFixedThreadPool(max_dispatcher);

        return executorService;
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncUncaughtExceptionHandler();
    }


}
