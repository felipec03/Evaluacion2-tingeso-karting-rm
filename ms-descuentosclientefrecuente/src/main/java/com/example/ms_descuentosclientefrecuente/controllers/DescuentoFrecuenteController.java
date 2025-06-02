package com.example.ms_descuentosclientefrecuente.controllers;

import com.example.ms_descuentosclientefrecuente.dtos.DescuentoFrecuenteRequestDto;
import com.example.ms_descuentosclientefrecuente.dtos.DescuentoFrecuenteResponseDto;
import com.example.ms_descuentosclientefrecuente.services.DescuentoFrecuenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/descuentos/frecuente")
public class DescuentoFrecuenteController {

    @Autowired
    private DescuentoFrecuenteService descuentoFrecuenteService;

    @PostMapping("/calcular")
    public ResponseEntity<DescuentoFrecuenteResponseDto> calcularDescuentoFrecuente(
            @RequestBody DescuentoFrecuenteRequestDto requestDto) {

        if (requestDto.getEmailArrendatario() == null || requestDto.getEmailArrendatario().isEmpty() ||
                requestDto.getFechaReservaActual() == null || requestDto.getPrecioInicial() <= 0) {
            return ResponseEntity.badRequest().build(); // Basic validation
        }

        DescuentoFrecuenteResponseDto response = descuentoFrecuenteService.calcularDescuento(requestDto);
        return ResponseEntity.ok(response);
    }
}