package com.example.ms_registroreserva_comprobantepago.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "ms-tarifadiaespecial", path = "/api/tarifas-dias-especiales")
public interface TarifaDiaEspecialFeignClient {

    @GetMapping("/aplicar")
    ResponseEntity<Double> aplicarTarifa(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("precioBase") Double precioBase,
            @RequestParam("cantidadPersonas") int cantidadPersonas,
            @RequestParam("cumpleanieros") int cumpleanieros);
}
