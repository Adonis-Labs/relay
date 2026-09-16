package com.example.relay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@EnableResilientMethods
public class RelayApplication {

	static void main(String[] args) {
		SpringApplication.run(RelayApplication.class, args);
	}

}
