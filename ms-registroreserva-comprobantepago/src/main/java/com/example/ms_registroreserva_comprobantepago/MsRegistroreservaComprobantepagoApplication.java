package com.example.ms_registroreserva_comprobantepago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;


@SpringBootApplication
public class MsRegistroreservaComprobantepagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRegistroreservaComprobantepagoApplication.class, args);
	}
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
