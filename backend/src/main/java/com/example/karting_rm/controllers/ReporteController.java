package com.example.karting_rm.controllers;

import com.example.karting_rm.dtos.ReporteFilaDTO;
import com.example.karting_rm.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/ingresos-por-tipo-reserva")
    public ResponseEntity<List<ReporteFilaDTO>> getReporteIngresosPorTipoReserva(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<ReporteFilaDTO> reporte = reporteService.generarReporteIngresosPorTipoReserva(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/ingresos-por-numero-personas")
    public ResponseEntity<List<ReporteFilaDTO>> getReporteIngresosPorNumeroPersonas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<ReporteFilaDTO> reporte = reporteService.generarReporteIngresosPorNumeroPersonas(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}