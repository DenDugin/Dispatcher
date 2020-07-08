package com.dispatcher.dispatcher.service.serviceImpl;

import com.dispatcher.dispatcher.component.Docker;
import com.dispatcher.dispatcher.entity.Message;
import com.dispatcher.dispatcher.exception.ConnectionTryException;
import com.dispatcher.dispatcher.exception.ReceiveMessageException;
import com.dispatcher.dispatcher.service.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
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

    private Logger logger = LogManager.getLogger();


    private Message createMessageDispatcher(Integer id_dispt, String message) throws JsonProcessingException {

        XmlMapper xmlMapper = new XmlMapper();
        Message messageDispt = xmlMapper.readValue(message, Message.class);

        if ( messageDispt == null ) throw new RuntimeException("Message is null");
        messageDispt.setDispatched_id(id_dispt);
        return messageDispt;
    }


    @Retryable(maxAttempts = 3, value = ConnectionTryException.class, backoff = @Backoff(delay = 5000))
    public void sendToRabbitData(Integer id_dispt, String message) throws ConnectionTryException, ReceiveMessageException, JsonProcessingException {
        try {
            Message messageDispt = createMessageDispatcher(id_dispt, message);
            // Если необходим ответ от Executor-а :
           // Boolean response = (Boolean)template.convertSendAndReceive(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);
           // if ( response == null || !response )  throw new ReceiveMessageException("Error from executor. Unable to process data.");

           //  Send and forget
            template.convertAndSend(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);

            logger.info("sendToRabbit by routing key : " + mapRoutKey.get(messageDispt.getTarget_id()));

        } catch (AmqpConnectException ex) {
            try {
                docker.startContainer(image);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            logger.error(ex.getMessage(),ex);
            throw new ConnectionTryException(ex.getMessage(),ex.getCause());
        }
    }



   @Async
   @Retryable(maxAttempts = 3, value = ConnectionTryException.class, backoff = @Backoff(delay = 5000))
    public void sendToRabbit(Integer id_dispt, String message) throws ConnectionTryException, ReceiveMessageException, JsonProcessingException {
        try {
            Message messageDispt = createMessageDispatcher(id_dispt,message);
            // Если необходим ответ от Executor-а :
           // Boolean response = (Boolean)template.convertSendAndReceive(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);
           //  if (response == null || !response)  throw new ReceiveMessageException("Error from executor while receive message");

            // Send and forget
            template.convertAndSend(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);

            logger.info("sendToRabbit by routing key : " + mapRoutKey.get(messageDispt.getTarget_id()));

        } catch (AmqpConnectException ex) {
            try {
                docker.startContainer(image);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            logger.error(ex.getMessage(),ex);
            throw new ConnectionTryException(ex.getMessage(),ex.getCause());
        }
    }



    @Async
    @Retryable(maxAttempts = 3, value = ConnectionTryException.class, backoff = @Backoff(delay = 5000))
    public Future<Boolean> sendToRabbitAndWait(Integer id_dispt, String message)  throws ConnectionTryException, ReceiveMessageException {
        try {
            Message messageDispt = createMessageDispatcher(id_dispt, message);
            // Если необходим ответ от Executor-а :
            Boolean response = (Boolean)template.convertSendAndReceive(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);
            if ( response == null || !response )  throw new ReceiveMessageException("Error from executor. Unable to process data.");
            // Send and forget
            // template.convertAndSend(mapRoutKey.get(messageDispt.getTarget_id()), messageDispt);

            logger.info("sendToRabbit by routing key : " + mapRoutKey.get(messageDispt.getTarget_id()));

        } catch (AmqpConnectException ex) {
            try {
                docker.startContainer(image);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            logger.error(ex.getMessage(),ex);
            throw new ConnectionTryException(ex.getMessage(),ex.getCause());
        } catch (ReceiveMessageException ex) {
            logger.error(ex.getMessage(),ex);
            throw ex;
        } catch (JsonProcessingException ex) {
            logger.error(ex.getMessage(),ex);
            throw new ReceiveMessageException("Error. Wrong input format", ex);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return new AsyncResult<Boolean>(false);
        }
        return new AsyncResult<Boolean>(true);
    }


}
