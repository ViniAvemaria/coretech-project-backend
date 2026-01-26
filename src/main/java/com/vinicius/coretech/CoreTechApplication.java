package com.vinicius.coretech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CoreTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreTechApplication.class, args);
	}
}
