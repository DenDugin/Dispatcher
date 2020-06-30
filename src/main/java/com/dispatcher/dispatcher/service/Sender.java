package com.dispatcher.dispatcher.service;

import java.util.concurrent.Future;

public interface Sender {

    void sendToRabbit(Integer id_dispt, String message);

    Future<Boolean> sendToRabbitAndWait(Integer id_dispt, String message);

}
