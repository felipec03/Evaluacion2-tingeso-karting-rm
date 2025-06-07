package com.example.ms_registroreserva_comprobantepago.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-descuentosclientefrecuente", path = "/api/descuentos")
public interface DescuentoFrecuenteFeignClient {

    @GetMapping("/actual/{rutCliente}")
    ResponseEntity<Double> obtenerDescuentoActual(@PathVariable("rutCliente") String rutCliente);
}
