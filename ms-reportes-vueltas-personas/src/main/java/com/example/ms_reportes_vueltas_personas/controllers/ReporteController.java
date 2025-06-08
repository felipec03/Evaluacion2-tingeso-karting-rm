package com.example.ms_reportes_vueltas_personas.controllers;

import com.example.ms_reportes_vueltas_personas.dto.ReporteResponseDTO;
import com.example.ms_reportes_vueltas_personas.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/ingresos-por-tarifa")
    public ResponseEntity<ReporteResponseDTO> getReporteIngresosPorTarifa(
            @RequestParam int anioInicio,
            @RequestParam int mesInicio,
            @RequestParam int anioFin,
            @RequestParam int mesFin) {

        // Validación básica de fechas (se puede mejorar)
        if (!isValidMonth(mesInicio) || !isValidMonth(mesFin) ||
                !isValidYear(anioInicio) || !isValidYear(anioFin) ||
                YearMonth.of(anioInicio, mesInicio).isAfter(YearMonth.of(anioFin, mesFin))) {
            return ResponseEntity.badRequest().build(); // O un DTO de error más específico
        }

        ReporteResponseDTO reporte = reporteService.generarReporteIngresosPorTarifa(anioInicio, mesInicio, anioFin, mesFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/ingresos-por-personas")
    public ResponseEntity<ReporteResponseDTO> getReporteIngresosPorNumeroPersonas(
            @RequestParam int anioInicio,
            @RequestParam int mesInicio,
            @RequestParam int anioFin,
            @RequestParam int mesFin) {

        if (!isValidMonth(mesInicio) || !isValidMonth(mesFin) ||
                !isValidYear(anioInicio) || !isValidYear(anioFin) ||
                YearMonth.of(anioInicio, mesInicio).isAfter(YearMonth.of(anioFin, mesFin))) {
            return ResponseEntity.badRequest().build();
        }

        ReporteResponseDTO reporte = reporteService.generarReporteIngresosPorNumeroPersonas(anioInicio, mesInicio, anioFin, mesFin);
        return ResponseEntity.ok(reporte);
    }

    private boolean isValidMonth(int mes) {
        return mes >= 1 && mes <= 12;
    }

    private boolean isValidYear(int anio) {
        // Ajusta según sea necesario, ej. no permitir años muy antiguos o futuros
        return anio >= 2000 && anio <= LocalDate.now().getYear() + 5;
    }
}
