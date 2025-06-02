package com.example.ms_tarifasconfig.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrecioCalculadoDTO {
    private Float precioBaseCalculado;
    private String detalleCalculo;
}