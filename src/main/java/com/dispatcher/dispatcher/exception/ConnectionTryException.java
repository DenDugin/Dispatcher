package com.dispatcher.dispatcher.exception;

import org.springframework.amqp.AmqpIOException;

import java.io.IOException;

public class ConnectionTryException extends AmqpIOException {

    public ConnectionTryException(IOException message) {
        super(message);
    }

    public ConnectionTryException(String message, Throwable cause) {
        super(message, cause);
    }


}
