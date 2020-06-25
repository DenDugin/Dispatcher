package com.dispatcher.dispatcher.component;

import com.dispatcher.dispatcher.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class Sender {


    @Autowired
    RabbitTemplate template;

    // private ConcurrentHashMap<Long,String> mapRoutKey = new ConcurrentHashMap<Long, String>(){{put(1l,"id_1");put(2l,"id_2");}};

    @Value("#{${simple.map}}")
    public HashMap<Integer,String> mapRoutKey;



    public void sendToRabbit(Order order) {


            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            Date date1 = new Date();

            // final CustomMessage message = new CustomMessage("Hello there!", new Random().nextInt(50), false);
            // template.convertAndSend("id_1", order);

           // template.convertSendAndReceive("id_1", order);
            System.out.println(mapRoutKey.get(order.getTarget_id()));

            template.convertAndSend(mapRoutKey.get(order.getTarget_id()), order);

            //System.out.println( (String) response );

            Date date = new Date();
            System.out.println("Response sendToRabbit :" + dateFormat.format(date));

    }



}
