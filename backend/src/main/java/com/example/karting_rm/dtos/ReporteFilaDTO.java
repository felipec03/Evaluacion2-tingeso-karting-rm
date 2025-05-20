package com.example.karting_rm.dtos;

import java.util.Map;
import java.util.LinkedHashMap; // To maintain month order

public class ReporteFilaDTO {
    private String categoria;
    private Map<String, Double> ingresosPorMes; // Key: "YYYY-MM", Value: monto

    public ReporteFilaDTO(String categoria) {
        this.categoria = categoria;
        this.ingresosPorMes = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order for months
    }

    // Getters and Setters
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Map<String, Double> getIngresosPorMes() {
        return ingresosPorMes;
    }

    public void setIngresosPorMes(Map<String, Double> ingresosPorMes) {
        this.ingresosPorMes = ingresosPorMes;
    }

    public void agregarIngreso(String mes, Double monto) {
        this.ingresosPorMes.put(mes, this.ingresosPorMes.getOrDefault(mes, 0.0) + monto);
    }

    public void inicializarMes(String mes) {
        this.ingresosPorMes.putIfAbsent(mes, 0.0);
    }
}