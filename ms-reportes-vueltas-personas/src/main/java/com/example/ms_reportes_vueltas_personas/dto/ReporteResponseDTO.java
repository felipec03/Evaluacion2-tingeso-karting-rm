package com.example.ms_reportes_vueltas_personas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {
    private List<String> mesesColumnas; // Lista de meses en formato "YYYY-MM" para las columnas
    private List<ReporteFilaDTO> filasReporte;
    private Map<String, Double> totalesPorMes; // Total general por cada mes
    private double granTotal; // Suma de todos los ingresos en el reporte
}
