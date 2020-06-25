package com.dispatcher.dispatcher.component;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfiguration {

   // Logger logger = Logger.getLogger(RabbitConfiguration.class);

    @Value("${rabbitmq.exchange}")
    private String exchange;


    //настраиваем соединение с RabbitMQ
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        // connectionFactory.setRequestedHeartBeat(60);
        return connectionFactory;
    }

//    @Bean
//    public AmqpAdmin amqpAdmin() {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
//        return rabbitAdmin;
//    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setReplyTimeout(10000);
        return rabbitTemplate;
    }


//    //объявляем очередь с именем queue1
//    @Bean
//    public Queue myQueue1() {
//        return new Queue("query-example-4-1");
//    }
//
//
//    @Bean
//    public Queue myQueue2() {
//        return new Queue("query-example-4-2");
//    }
//
//
//    @Bean
//    public DirectExchange directExchange(){
//        return new DirectExchange("exchange-example-4");
//    }
//
//
//    @Bean
//    public Binding errorBinding1(){
//        return BindingBuilder.bind(myQueue1()).to(directExchange()).with("error");
//    }
//
//    @Bean
//    public Binding errorBinding2(){
//        return BindingBuilder.bind(myQueue2()).to(directExchange()).with("error");
//    }
//
//
//    @Bean
//    public Binding infoBinding(){
//        return BindingBuilder.bind(myQueue2()).to(directExchange()).with("info");
//    }
//
//    @Bean
//    public Binding warningBinding(){
//        return BindingBuilder.bind(myQueue2()).to(directExchange()).with("warning");
//    }



}
