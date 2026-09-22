package com.example.relay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.resilience.annotation.EnableResilientMethods;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableResilientMethods
@EnableJpaAuditing
public class RelayApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(RelayApplication.class, args);
		printUrl(context.getEnvironment());
	}

	private static void printUrl(Environment env) {
		String port = env.getProperty("server.port", "8080");
		String contextPath = env.getProperty("server.servlet.context-path", "");
		String host;
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			host = "localhost";
		}
		System.out.println("Local:    http://localhost:" + port + contextPath);
		System.out.println("External: http://" + host + ":" + port + contextPath);
	}
}
