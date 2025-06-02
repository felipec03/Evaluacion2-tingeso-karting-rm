package com.example.ms_descuentoporpersona.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoCalculadoDTO {
    private Float montoDescuento;
    private Float porcentajeAplicado;
    private String detalle;
}