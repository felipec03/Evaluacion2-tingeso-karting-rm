package com.example.ms_registroreserva_comprobantepago.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CrearReservaDTO {
    @NotNull(message = "ID de cliente no puede ser nulo")
    private Long idCliente;

    @NotNull(message = "ID de habitación no puede ser nulo")
    private Long idHabitacion;

    @NotNull(message = "Fecha de entrada no puede ser nula")
    @FutureOrPresent(message = "Fecha de entrada debe ser hoy o en el futuro")
    private LocalDate fechaEntrada;

    @NotNull(message = "Fecha de salida no puede ser nula")
    @Future(message = "Fecha de salida debe ser en el futuro")
    private LocalDate fechaSalida;

    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    private int numeroHuespedes;

    @NotNull(message = "Precio total no puede ser nulo")
    @Min(value = 0, message = "Precio total no puede ser negativo")
    private BigDecimal precioTotal; // En un sistema real, esto podría calcularse en el backend
}
