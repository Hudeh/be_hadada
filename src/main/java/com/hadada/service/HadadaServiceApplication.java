package com.hadada.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HadadaServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(HadadaServiceApplication.class, args);
	}
}