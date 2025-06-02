package com.example.ms_tarifasconfig.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionGeneralDTO {
    private Long id;
    private int duracionMinimaHorasReserva;
    private int duracionMaximaHorasReserva;
    private int intervaloPermitidoHorasReserva;
}