package com.dispatcher.dispatcher;

//import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class DispatcherApplication {

	public static void main(String[] args) {
		//BasicConfigurator.configure();
		SpringApplication.run(DispatcherApplication.class, args);
	}

}
