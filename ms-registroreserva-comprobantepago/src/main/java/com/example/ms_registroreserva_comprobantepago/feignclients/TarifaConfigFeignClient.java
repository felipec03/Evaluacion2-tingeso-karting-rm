package com.example.ms_registroreserva_comprobantepago.feignclients;


import com.example.ms_registroreserva_comprobantepago.dtos.TarifaDTO; // Assuming a DTO for TarifaEntity
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-tarifasconfig", path = "/api/tarifas")
public interface TarifaConfigFeignClient {

    @GetMapping("/tipo/{tipoReserva}")
    ResponseEntity<TarifaDTO> getTarifaByTipoReserva(@PathVariable("tipoReserva") Integer tipoReserva);
}
