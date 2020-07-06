package com.dispatcher.dispatcher.service;

import com.dispatcher.dispatcher.exception.ConnectionTryException;
import com.dispatcher.dispatcher.exception.ReceiveMessageException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.Future;

public interface Sender {

    void sendToRabbit(Integer id_dispt, String message) throws ConnectionTryException, JsonProcessingException;

    Future<Boolean> sendToRabbitAndWait(Integer id_dispt, String message) throws ConnectionTryException, JsonProcessingException;

    void sendToRabbitData(Integer id_dispt, String message) throws ConnectionTryException, ReceiveMessageException, JsonProcessingException;

}
