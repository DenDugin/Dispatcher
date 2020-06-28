FROM openjdk:8-jdk-alpine

EXPOSE 8080

ADD target/application.properties application.properties
ADD target/dispatcher.jar dispatcher.jar

ENTRYPOINT ["java","-jar","dispatcher.jar"]
