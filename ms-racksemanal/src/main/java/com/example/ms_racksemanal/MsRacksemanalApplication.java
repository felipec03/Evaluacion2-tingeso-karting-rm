package com.example.ms_racksemanal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsRacksemanalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRacksemanalApplication.class, args);
	}

}
