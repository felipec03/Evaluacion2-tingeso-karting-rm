package com.example.ms_reportes_vueltas_personas.feignclient;

import com.example.ms_reportes_vueltas_personas.dto.ReservaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Si usas Eureka: @FeignClient(name = "ms-registroreserva-comprobante")
// Si no usas Eureka y especificas URL en properties:
@FeignClient(name = "ms-registroreserva-comprobantepago", path = "/api/reservas")
public interface RegistroReservaFeignClient {
    @GetMapping("/")
    List<ReservaDTO> obtenerTodasLasReservas();
}
