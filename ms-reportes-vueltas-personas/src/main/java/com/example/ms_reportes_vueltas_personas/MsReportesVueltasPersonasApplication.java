package com.example.ms_reportes_vueltas_personas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
@EnableFeignClients // Habilitar Feign Clients
public class MsReportesVueltasPersonasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsReportesVueltasPersonasApplication.class, args);
	}

}
