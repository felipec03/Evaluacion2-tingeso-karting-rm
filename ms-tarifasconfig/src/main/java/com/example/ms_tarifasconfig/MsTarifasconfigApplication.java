package com.example.ms_tarifasconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsTarifasconfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTarifasconfigApplication.class, args);
	}

}
