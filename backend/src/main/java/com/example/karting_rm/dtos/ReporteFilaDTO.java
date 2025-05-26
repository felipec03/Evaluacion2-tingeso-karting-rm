package com.example.karting_rm.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.LinkedHashMap; // To maintain month order

@Setter
@Getter
public class ReporteFilaDTO {
    // Getters and Setters
    private String categoria;
    private Map<String, BigDecimal> ingresosPorMes; // Key: "YYYY-MM", Value: monto

    public ReporteFilaDTO(String categoria) {
        this.categoria = categoria;
        this.ingresosPorMes = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order for months
    }

    public void agregarIngreso(String mes, BigDecimal monto) {
        this.ingresosPorMes.put(mes, this.ingresosPorMes.getOrDefault(mes, BigDecimal.ZERO).add(monto));
    }

    public void inicializarMes(String mes) {
        this.ingresosPorMes.putIfAbsent(mes, BigDecimal.ZERO);
    }
}