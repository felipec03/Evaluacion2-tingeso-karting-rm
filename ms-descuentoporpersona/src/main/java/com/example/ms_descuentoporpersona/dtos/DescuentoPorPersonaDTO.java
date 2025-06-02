package com.example.ms_descuentoporpersona.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoPorPersonaDTO {
    private Long id;
    private Integer minPersonas;
    private Integer maxPersonas;
    private Float porcentajeDescuento;
    private String descripcion;
    private Boolean activo;
}