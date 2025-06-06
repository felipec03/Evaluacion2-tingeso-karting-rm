package com.example.ms_descuentosclientefrecuente.controllers;


import com.example.ms_descuentosclientefrecuente.services.DescuentoFrecuenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/descuentos")
public class DescuentoFrecuenteController {

    @Autowired
    private DescuentoFrecuenteService descuentoService;

    /**
     * Obtiene el porcentaje de descuento para un cliente en un mes y año específicos
     */
    @GetMapping("/{rutCliente}")
    public ResponseEntity<Double> obtenerDescuento(
            @PathVariable String rutCliente,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {

        // Si no se especifican mes y año, usar el mes y año actuales
        if (mes == null || anio == null) {
            LocalDate fechaActual = LocalDate.now();
            mes = mes != null ? mes : fechaActual.getMonthValue();
            anio = anio != null ? anio : fechaActual.getYear();
        }

        Double porcentajeDescuento = descuentoService.calcularDescuento(rutCliente, mes, anio);
        return ResponseEntity.ok(porcentajeDescuento);
    }

    /**
     * Obtiene el porcentaje de descuento actual para un cliente
     */
    @GetMapping("/actual/{rutCliente}")
    public ResponseEntity<Double> obtenerDescuentoActual(@PathVariable String rutCliente) {
        Double porcentajeDescuento = descuentoService.obtenerDescuentoActual(rutCliente);
        return ResponseEntity.ok(porcentajeDescuento);
    }
}