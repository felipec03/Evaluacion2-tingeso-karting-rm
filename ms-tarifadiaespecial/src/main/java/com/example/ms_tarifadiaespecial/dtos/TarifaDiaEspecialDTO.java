package com.example.ms_tarifadiaespecial.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDiaEspecialDTO {
    private Long id;
    private LocalDate fecha;
    private String descripcion;
    private String tipoTarifa; // "FIJA", "PORCENTUAL_DESCUENTO", "PORCENTUAL_RECARGO"
    private Double valor;
}