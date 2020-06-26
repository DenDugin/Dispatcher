package com.dispatcher.dispatcher.component;

import com.dispatcher.dispatcher.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class Sender {


    @Autowired
    private RabbitTemplate template;

    @Value("#{${simple.map}}")
    public ConcurrentHashMap<Integer,String> mapRoutKey;



    public void sendToRabbit(Order order) {

            // Если необходим ответ от Executor-а :
            // template.convertSendAndReceive("id_1", order);

            System.out.println(mapRoutKey.get(order.getTarget_id()));

            template.convertAndSend(mapRoutKey.get(order.getTarget_id()), order);


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            Date date = new Date();
            System.out.println("Response sendToRabbit :" + dateFormat.format(date));


    }



}
