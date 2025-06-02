package com.example.ms_descuentosclientefrecuente.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoFrecuenteResponseDto {
    private double descuentoAplicado;
    private boolean esClienteFrecuente;
    private String emailArrendatario;
    private int reservasPreviasEnPeriodo;
}
