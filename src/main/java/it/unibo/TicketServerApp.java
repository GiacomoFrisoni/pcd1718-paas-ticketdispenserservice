package it.unibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication // tells Boot this is the bootstrap class for the project
@RefreshScope
public class TicketServerApp {

	public static void main(String[] args) {
		SpringApplication.run(TicketServerApp.class, args); // starts the Spring Boot service
	}
	
}
