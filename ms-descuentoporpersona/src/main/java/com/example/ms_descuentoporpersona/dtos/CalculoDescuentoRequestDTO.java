package com.example.ms_descuentoporpersona.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculoDescuentoRequestDTO {
    private Integer numeroPersonas;
    private Float precioInicial;
}