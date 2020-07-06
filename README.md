# Dispatcher
### Схема работы сервиса
![scheme](scheme.png)

Все исполнители также могут быть запущены в одном контейнере.

В качестве брокера сообщений был выбран RabbitMQ. Тип Exchange - Direct.
Также выполнена реализация с брокером Kafka.


**Dispatcher Dockerfile :**

FROM openjdk:8-jdk-alpine

EXPOSE 8080  
ADD target/application.properties application.properties  
ADD target/dispatcher.jar dispatcher.jar  
ENTRYPOINT ["java","-jar","dispatcher.jar"]

**Link dockerhub** :  docker push denisdugin/dispatcher:2

RabbitMQ

**Link dockerhub** :  docker push denisdugin/myrabbitmq:latest


**Dispatcher API** : 

POST /api/dispatcher  
consumes = APPLICATION_XML_VALUE    
Пример входящего XML :    
<message>  
<target_id>1</target_id>  
<data>TAXI</data>  
<dispatched_id>1</dispatched_id>  
<client_id>123</client_id>  
</message>  


**Достоинства :**
- Реализованы несколько типов доставки сообщений (снхронный и аснихронный).   
- Rabbit : Легковесный прокол AMQP. Высокая отказоустойчивость и производительность. Встроенный роутинг сообщений с помощью Exchange.  
- Независимость модулей Executor, Dispatсher.
- Динамическое добавление исполнителей.


**Возможные доработки :**  
- Если есть безработные(простаивающие) executor-ы, то перевешать часть задач на них.  
- Доделать обработку Dead Letter Queue Messages.  
- Доработки в части приема ответа от исполнителя.  
- Использование опции prefetchCount.
