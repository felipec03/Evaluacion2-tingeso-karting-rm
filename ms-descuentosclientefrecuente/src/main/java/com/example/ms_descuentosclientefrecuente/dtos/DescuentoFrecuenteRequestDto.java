package com.example.ms_descuentosclientefrecuente.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoFrecuenteRequestDto {
    private String emailArrendatario;
    private LocalDate fechaReservaActual; // The date for which the discount is being calculated
    private double precioInicial;
}
