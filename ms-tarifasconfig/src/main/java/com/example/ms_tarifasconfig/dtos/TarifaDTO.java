package com.example.ms_tarifasconfig.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDTO {
    private Long id;
    private Integer tipoReserva;
    private String descripcion;
    private Double precioBasePorPersona;
    private Boolean activa;
}