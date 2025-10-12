package com.amdocs.schedulease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduleaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleaseApplication.class, args);
	}
}
