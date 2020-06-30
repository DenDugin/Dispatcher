package com.dispatcher.dispatcher.service.serviceImpl;

import com.dispatcher.dispatcher.component.Docker;
import com.dispatcher.dispatcher.entity.Message;
import com.dispatcher.dispatcher.service.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;


@Service
public class SenderImpl implements Sender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Docker docker;

    @Value("${rabbitmq.image}")
    private String image;

    @Value("#{${routKey.map}}")
    public ConcurrentHashMap<Integer,String> mapRoutKey;

    private Logger logger =  LogManager.getLogger();


    private Message createMessageDispatcher(Integer id_dispt, String message) throws JsonProcessingException {

        XmlMapper xmlMapper = new XmlMapper();
        Message messageDispt = xmlMapper.readValue(message, Message.class);

        if ( messageDispt == null ) throw new RuntimeException("Message is null");
        messageDispt.setDispatched_id(id_dispt);
        return messageDispt;
    }


    @Async
    public void sendToRabbit(Integer id_dispt, String message) {

        try {
            Message messageDispt = createMessageDispatcher(id_dispt,message);
            // Если необходим ответ от Executor-а :
            // template.convertSendAndReceive("id_1", message);
            template.convertAndSend(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);

            logger.info("sendToRabbit by routing key : " + mapRoutKey.get(messageDispt.getTarget_id()));

        } catch (AmqpIOException ex) {
            if ( ex.getCause() instanceof SocketTimeoutException | ex.getCause() instanceof NoRouteToHostException) {
                try {
                    docker.startContainer(image);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
                // throw new ConnectionTryException(ex.getMessage(),ex.getCause());
            }
            logger.error(ex.getMessage(),ex);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
    }



    @Async
    public Future<Boolean> sendToRabbitAndWait(Integer id_dispt, String message) {

        try {
            Message messageDispt = createMessageDispatcher(id_dispt, message);
            // Если необходим ответ от Executor-а :
            // template.convertSendAndReceive("id_1", message);
            template.convertAndSend(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);

            logger.info("sendToRabbit by routing key : " + mapRoutKey.get(messageDispt.getTarget_id()));

        } catch (AmqpIOException ex) {
            if ( ex.getCause() instanceof SocketTimeoutException | ex.getCause() instanceof NoRouteToHostException) {
                try {
                    docker.startContainer(image);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
            logger.error(ex.getMessage(),ex);
            return new AsyncResult<Boolean>(false);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return new AsyncResult<Boolean>(false);
        }
        return new AsyncResult<Boolean>(true);
    }

}
