package com.example.ms_racksemanal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    private Long id;
    private String nombreCliente;
    private String rutCliente;
    private LocalDate fechaHora;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
}