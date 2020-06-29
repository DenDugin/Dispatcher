package com.dispatcher.dispatcher.controller;

import com.dispatcher.dispatcher.component.Docker;
import com.dispatcher.dispatcher.component.Sender;
import com.dispatcher.dispatcher.entity.Message;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;


@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private Sender sender;

    @Autowired
    private Docker docker;

    @Value("${rabbitmq.image}")
    private String image;

    Logger logger =  LogManager.getLogger();

    private int max_dispatcher = 100;

    private final ExecutorService es = Executors.newFixedThreadPool(max_dispatcher);

    private BlockingQueue<Integer> idQueue;

    public MainController() throws InterruptedException {

        idQueue = new ArrayBlockingQueue<>(max_dispatcher);

        for (int i=1; i<=max_dispatcher; i++)
            idQueue.put(i);
    }


    @PostMapping(value = "/dispatcher", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> geMessage(@RequestBody String message) throws Exception {

        Integer id_dispt = idQueue.take();


      // т.к. необходимо моментально принять и ответить на сообщение, то формируем пул потоков для создания заказа и отправки его исполнителю
         es.submit(() -> {
             try {
                 XmlMapper xmlMapper = new XmlMapper();
                 Message readValue = xmlMapper.readValue(message, Message.class);

                 if ( readValue == null ) throw new RuntimeException("Message is null");

                 readValue.setDispatched_id(id_dispt);

                 sender.sendToRabbit(readValue);

             } catch (AmqpIOException ex) {
                 if ( ex.getCause() instanceof SocketTimeoutException | ex.getCause() instanceof NoRouteToHostException) {
                     try {
                         docker.startContainer(image);
                     } catch (IOException e) {
                         logger.error(e.getMessage(),e);
                     }
                     // throw new ConnectionTryException(ex.getMessage(),ex.getCause());
                 }
                 logger.error(ex.getMessage(),ex);
             }
             catch (Exception ex) {
                 logger.error(ex.getMessage(),ex);
             }
         });


        logger.info("geMessage");

        idQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }



    // Если необходим ответ от исполнителя или дождаться отправки сообщения исполнителю и затем отправить ответ клиенту
    @PostMapping(value = "/disp/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> waitMessage(@RequestBody String message, @PathVariable Integer id) throws InterruptedException, ExecutionException {

        Integer id_dispt = idQueue.take();

        Future<Integer> result = es.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    Message readValue = xmlMapper.readValue(message, Message.class);

                    readValue.setDispatched_id(id_dispt);

                    sender.sendToRabbit(readValue);

                    return id_dispt;

                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                    return null;
                }
            }

        });

        result.get();

        logger.info("waitMessage");

        idQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }


}
