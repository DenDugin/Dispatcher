package com.dispatcher.dispatcher.component;

import com.dispatcher.dispatcher.entity.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class Sender {


    @Autowired
    private RabbitTemplate template;

    @Value("#{${routKey.map}}")
    public ConcurrentHashMap<Integer,String> mapRoutKey;

    Logger logger =  LogManager.getLogger();



    public void sendToRabbit( Message message ) {

            // Если необходим ответ от Executor-а :
            // template.convertSendAndReceive("id_1", message);

            template.convertAndSend(mapRoutKey.get(message.getTarget_id()), message);

            logger.info("sendToRabbit by routing key : " + mapRoutKey.get(message.getTarget_id()));

    }



}
