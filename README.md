# Dispatcher
### Схема работы сервиса
![scheme](scheme.png)

Все исполнители также могут быть запущены в одном контейнере. 


**Dispatcher Dockerfile :**

FROM openjdk:8-jdk-alpine

EXPOSE 8080  
ADD target/application.properties application.properties  
ADD target/dispatcher.jar dispatcher.jar  
ENTRYPOINT ["java","-jar","dispatcher.jar"]

**Link** :  docker push denisdugin/dispatcher:latest


**Executor Dockerfile :**

FROM openjdk:8-jdk-alpine  
EXPOSE 8080  
ADD target/application.properties application.properties  
ADD target/executor-0.0.1-SNAPSHOT.jar executor-0.0.1-SNAPSHOT.jar  
ENTRYPOINT ["java","-jar","executor.jar"]

**Link** :  docker push denisdugin/executor:latest


**Dockerfile for mylti start Executors :** 
FROM openjdk:8-jdk-alpine  
EXPOSE 8080  
ADD target/application.properties application.properties  
ADD target/application.properties2 application.properties2
ADD target/application.properties3 application.properties3  
ADD target/executor-0.0.1-SNAPSHOT.jar executor.jar
ADD start.sh start.sh  
CMD ["sh", "start.sh"]



**docker-compose.yml**

version: '3.3'

services:

networks:
  app-tier:
    driver: bridge

services:

  rabbitmq:
    image: 'bitnami/rabbitmq:latest'
    networks:
      - app-tier
  disp:
     container_name: dispatcher
     build: ./Dispatcher
     ports:
      - "8080:8080"
     image: dispatcher
     networks:
      - app-tier


  java:
     container_name: executor
     volumes:
      - ./Save/id_1:/id_1
     build: ./Executor
     ports:
      - "8081:8081"
     image: executor
     networks:
      - app-tier

  java2:
     container_name: executor2
     volumes:
      - ./Save/id_2:/id_2
     build: ./Executor2
     ports:
      - "8082:8082"
     image: executor2
     networks:
      - app-tier


