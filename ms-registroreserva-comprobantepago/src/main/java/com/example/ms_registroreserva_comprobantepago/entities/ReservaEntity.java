package com.example.ms_registroreserva_comprobantepago.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora; // Fecha y hora de inicio de la reserva
    private int tipoReserva; // Ejemplo: 1=Normal, 2=Extendida, etc. Define precio y quizás duración
    private int cantidadPersonas;
    private int cantidadCumple; // Número de personas que están de cumpleaños

    private String nombreUsuario;
    private String rutUsuario;
    private String emailUsuario;
    private String telefonoUsuario;


    // Campos adicionales para la lógica de negocio y persistencia de cálculos
    private Double montoBase;
    private Double porcentajeDescuentoAplicado;
    private Double montoDescuento;
    private Double montoFinal;
    private String estadoReserva; // PENDIENTE, CONFIRMADA, PAGADA, CANCELADA

    private Integer duracionMinutos = 0; // Duración de la reserva en minutos
}