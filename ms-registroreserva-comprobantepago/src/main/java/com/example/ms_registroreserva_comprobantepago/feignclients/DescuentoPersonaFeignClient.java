package com.example.ms_registroreserva_comprobantepago.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-descuentoporpersona", path = "/api/descuento-persona")
public interface DescuentoPersonaFeignClient {

    @GetMapping("/calcular/{numeroPersonas}")
    ResponseEntity<Double> calcularDescuentoPorPersonas(@PathVariable("numeroPersonas") int numeroPersonas);
}