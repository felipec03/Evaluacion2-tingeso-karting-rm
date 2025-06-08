package com.example.ms_reportes_vueltas_personas.feignclient;

import com.example.ms_reportes_vueltas_personas.dto.ReservaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// Si usas Eureka: @FeignClient(name = "ms-registroreserva-comprobante")
// Si no usas Eureka y especificas URL en properties:
@FeignClient(name = "ms-registroreserva-comprobante", url = "${app.feign.client.ms-registroreserva-comprobante.url}")
public interface RegistroReservaFeignClient {
    @GetMapping("/api/reservas/")
    List<ReservaDTO> obtenerTodasLasReservas();

}
