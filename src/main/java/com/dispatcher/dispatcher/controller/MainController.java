package com.dispatcher.dispatcher.controller;


import com.dispatcher.dispatcher.service.Sender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.*;


@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private Sender sender;

    private Logger logger =  LogManager.getLogger();

    private final int max_dispatcher = 100;

    private BlockingQueue<Integer> idQueue;

    public MainController() throws InterruptedException {
       idQueue = new ArrayBlockingQueue<>(max_dispatcher);
        for (int i=1; i<=max_dispatcher; i++)
            idQueue.put(i);
    }


    @PostMapping(value = "/dispatcher", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> getMessage(@RequestBody String message) throws Exception {

        Integer id_dispt = idQueue.take();

// 1)        т.к. необходимо моментально принять и ответить на сообщение, то формируем пул потоков для создания заказа и отправки его исполнителю
          sender.sendToRabbit(id_dispt, message);


// 2)      Если необходим ответ от исполнителя или дождаться отправки сообщения исполнителю и затем отправить ответ клиенту
//         Future<Boolean> futureResult = sender.sendToRabbitAndWait(id_dispt, message);
//        if (!futureResult.get()) {
//            logger.info( "Message don't send");
//            throw new RuntimeException("Message don't send");
//        }


// 3)   обычный метод отправки данных
//        sender.sendToRabbitData(id_dispt, message);

        logger.info("getMessage");

        idQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }


}
