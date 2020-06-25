package com.dispatcher.dispatcher.controller;

import com.dispatcher.dispatcher.component.Sender;
import com.dispatcher.dispatcher.entity.Message;
import com.dispatcher.dispatcher.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

@RestController
//@Scope(value = "request")  // если request - то потоки не останавливаются
@RequestMapping("/api")
public class MainController {

    @Autowired
    private Sender sender;

    private int max_dispatcher = 100;

    private BlockingQueue<Integer> blockingQueue;

    public MainController() throws InterruptedException {

        blockingQueue = new ArrayBlockingQueue<>(max_dispatcher);

        for (int i=1; i<=max_dispatcher; i++)
            blockingQueue.put(i);
    }

    private final ExecutorService es = Executors.newFixedThreadPool(max_dispatcher);   // ограничиваем число запросов к сервису


    @PostMapping(value = "/dispatcher", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> geMessage(@RequestBody String message) throws InterruptedException {

        Integer id_dispt = blockingQueue.take();

         es.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    Message readValue = xmlMapper.readValue(message, Message.class);

//                    System.out.println(readValue.getId());
//                    System.out.println(readValue.getData());

                    Order order = new Order(readValue.getId(), id_dispt, readValue.getData(),readValue.getClient_id());

                    sender.sendToRabbit(order);

                    //Sender.sendPost(order);
                    //  Thread.sleep(15000);
                    // запуск исполнителя
                   // System.out.println(id_dispt);
                   // return id_dispt;

                } catch (Exception e) {
                   // return null;
                }
            }
        });


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = new Date();
        System.out.println("After GET :" + dateFormat.format(date));

            blockingQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }



    @PostMapping(value = "/disp/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Integer> getMessage2(@RequestBody String message, @PathVariable Integer id) throws InterruptedException, ExecutionException {

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

        blockingQueue.put(id_dispt);
        return new ResponseEntity<Integer>(id_dispt, HttpStatus.OK);
    }





}
