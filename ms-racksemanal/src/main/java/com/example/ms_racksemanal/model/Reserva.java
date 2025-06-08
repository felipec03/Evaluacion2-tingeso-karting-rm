package com.example.ms_racksemanal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reserva {
    private Long id;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private Integer tipoReserva;
    private Integer cantidadPersonas;
    private Integer cantidadCumple;

    private String nombreUsuario;
    private String rutUsuario;
    private String emailUsuario;
    private String telefonoUsuario;

    private Double montoBase;
    private Double porcentajeDescuentoAplicado;
    private Double montoDescuento;
    private Double montoFinal;
    private String estadoReserva;

    // Helper method for compatibility with existing code
    public String getEstado() {
        return estadoReserva;
    }

    // Helper method if your code expects rutCliente
    public String getRutCliente() {
        return rutUsuario;
    }

    // Helper method if your code expects nombreCliente
    public String getNombreCliente() {
        return nombreUsuario;
    }
}