package com.example.ms_racksemanal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Important for flexibility with Feign client
public class Reserva {

    private Long id;
    private LocalDateTime fechaHora;
    private int tipoReserva;
    private int cantidadPersonas;
    private int cantidadCumple;

    private String nombreUsuario;
    private String rutUsuario;
    private String emailUsuario;
    private String telefonoUsuario;

    private Double montoBase;
    private Double porcentajeDescuentoAplicado;
    private Double montoDescuento;
    private Double montoFinal;
    private String estadoReserva;

    private Integer duracionMinutos;
}