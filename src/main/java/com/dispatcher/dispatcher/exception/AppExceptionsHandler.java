package com.dispatcher.dispatcher.exception;


import com.dispatcher.dispatcher.component.Docker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.Date;


@ControllerAdvice
public class AppExceptionsHandler {

    @Autowired
    private Docker docker;

    @Value("${rabbitmq.image}")
    private String image;


    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(ConnectionTryException  exc) {

        ErrorMessage errorResponse = new ErrorMessage();
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setMessage(exc.getMessage());
        errorResponse.setTimestamp(new Date());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(ReceiveMessageException exc) {

        ErrorMessage errorResponse = new ErrorMessage();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(exc.getMessage());
        errorResponse.setTimestamp(new Date());
        errorResponse.setError(Arrays.toString(exc.getStackTrace()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(Throwable exc) {

        ErrorMessage errorResponse = new ErrorMessage();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(exc.getMessage());
        errorResponse.setTimestamp(new Date());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

}
