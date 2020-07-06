package com.dispatcher.dispatcher.exception;

public class ReceiveMessageException extends RuntimeException {

    public ReceiveMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiveMessageException(String message) {
        super(message);
    }
}
