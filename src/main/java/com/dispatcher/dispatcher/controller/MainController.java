package com.dispatcher.dispatcher.controller;

import com.dispatcher.dispatcher.component.Docker;
import com.dispatcher.dispatcher.component.Sender;
import com.dispatcher.dispatcher.entity.Message;
import com.dispatcher.dispatcher.entity.Order;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.amqp.AmqpIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

@RestController
//@Scope(value = "request")
@RequestMapping("/api")
public class MainController {

    @Autowired
    private Sender sender;

    @Autowired
    private Docker docker;

    @Value("${rabbitmq.image}")
    private String image;

    Logger logger = Logger.getLogger(MainController.class);

    private int max_dispatcher = 100;

    private final ExecutorService es = Executors.newFixedThreadPool(max_dispatcher);   // ограничиваем число запросов к сервису

    private BlockingQueue<Integer> blockingQueue;

    public MainController() throws InterruptedException {

        blockingQueue = new ArrayBlockingQueue<>(max_dispatcher);

        for (int i=1; i<=max_dispatcher; i++)
            blockingQueue.put(i);
    }


    @PostMapping(value = "/dispatcher", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> geMessage(@RequestBody String message) throws Exception {

        Integer id_dispt = blockingQueue.take();

         es.submit(() -> {
             try {
                 XmlMapper xmlMapper = new XmlMapper();
                 Message readValue = xmlMapper.readValue(message, Message.class);

                 Order order = new Order(readValue.getId(), id_dispt, readValue.getData(),readValue.getClient_id());

                 sender.sendToRabbit(order);

             } catch (AmqpIOException ex) {
                 if ( ex.getCause() instanceof SocketTimeoutException | ex.getCause() instanceof NoRouteToHostException) {
                     try {
                         docker.startContainer(image);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                     // throw new ConnectionTryException(ex.getMessage(),ex.getCause());
                 }
                 ex.printStackTrace();
             }
             catch (Exception ex) {
                 ex.printStackTrace();
             }
         });


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = new Date();
        logger.info("geMessage :" + dateFormat.format(date));

        blockingQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }




    // Если необходим ответ от исполнителя или дождаться отправки сообщения исполнителю и затем отправить ответ клиенту
    @PostMapping(value = "/disp/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> waitMessage(@RequestBody String message, @PathVariable Integer id) throws InterruptedException, ExecutionException {

        Integer id_dispt = blockingQueue.take();

        Future<Integer> result = es.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    Message readValue = xmlMapper.readValue(message, Message.class);

                    Order order = new Order(readValue.getId(), id_dispt, readValue.getData(),readValue.getClient_id());

                    sender.sendToRabbit(order);

                    return id_dispt;

                } catch (Exception e) {
                    return null;
                }
            }

        });

        result.get();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = new Date();
        logger.info("geMessage :" + dateFormat.format(date));

        blockingQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }


}
