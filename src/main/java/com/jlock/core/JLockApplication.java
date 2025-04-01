package com.jlock.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(JLockApplication.class, args);
	}
}
