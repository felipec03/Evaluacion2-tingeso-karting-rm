package com.example.ms_reportes_vueltas_personas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFilaDTO {
    private String categoria; // Ej: "10 Vueltas" o "1-3 Personas"
    private Map<String, Double> ingresosPorMes; // Key: "YYYY-MM", Value: Monto
    private double totalIngresosCategoria; // Suma de todos los meses para esta categoría
}