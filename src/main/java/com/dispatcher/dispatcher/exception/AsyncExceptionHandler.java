package com.dispatcher.dispatcher.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private Logger logger =  LogManager.getLogger();

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        logger.error("Method Name::"+method.getName());
        logger.error(ex.getMessage(),ex);

    }

}
