package com.example.ms_registroreserva_comprobantepago.dtos;

import com.example.ms_registroreserva_comprobantepago.entities.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaDTO {
    private Long id;
    private Long idCliente;
    private Long idHabitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int numeroHuespedes;
    private BigDecimal precioTotal;
    private EstadoReserva estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
