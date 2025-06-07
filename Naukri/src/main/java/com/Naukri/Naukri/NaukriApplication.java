package com.Naukri.Naukri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NaukriApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(NaukriApplication.class, args);
			}

}
