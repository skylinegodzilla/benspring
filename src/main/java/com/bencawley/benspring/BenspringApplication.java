package com.bencawley.benspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// todo start writting the tests before you regret it and brake things.

@SpringBootApplication(
		exclude = { SecurityAutoConfiguration.class }
)
public class BenspringApplication {
	public static void main(String[] args) {
		SpringApplication.run(BenspringApplication.class, args);
	}
}

