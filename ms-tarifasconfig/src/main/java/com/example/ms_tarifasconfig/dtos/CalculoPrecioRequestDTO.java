package com.example.ms_tarifasconfig.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculoPrecioRequestDTO {
    private Integer tipoReserva;
    private Integer numeroPersonas;
    private LocalDateTime fechaHoraInicio;
}