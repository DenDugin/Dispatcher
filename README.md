# Dispatcher
### Схема работы сервиса
![scheme](scheme.png)

В качестве брокера сообщений был выбран RabbitMQ. Каждому исполнителю соответствует своя очередь. Тип Exchange - Direct.
Также выполнена реализация с брокером Kafka. Все исполнители также могут быть запущены в одном контейнере.


**Dispatcher Dockerfile :**

FROM openjdk:8-jdk-alpine

EXPOSE 8080  
ADD target/application.properties application.properties  
ADD target/dispatcher.jar dispatcher.jar  
ENTRYPOINT ["java","-jar","dispatcher.jar"]

**Link dockerhub** :  docker push denisdugin/dispatcher:3

RabbitMQ

**Link dockerhub** :  docker push denisdugin/myrabbitmq:latest


**Dispatcher API** : 

POST /api/dispatcher  
consumes = APPLICATION_XML_VALUE    
Пример входящего XML :    
```xml
<Message>
<target_id>4</target_id>
<data>Need taxi</data>
<client_id>345</client_id>
</Message>
```

**Достоинства :**
- Реализованы несколько типов доставки сообщений(синхронный и асинхронный).   
- Rabbit : Легковесный прокол AMQP. Высокая отказоустойчивость и производительность. Встроенный роутинг сообщений с помощью Exchange.  
- Независимость модулей Executor, Dispatсher.
- Динамическое добавление исполнителей.


**Возможные доработки :**  
- Если есть безработные(простаивающие) executor-ы, то перевешать часть задач на них.  
- Доделать обработку Dead Letter Queue Messages.  
- Доработки в части приема ответа от исполнителя.  
- Использование опции prefetchCount.
- Использование Kubernetes.
