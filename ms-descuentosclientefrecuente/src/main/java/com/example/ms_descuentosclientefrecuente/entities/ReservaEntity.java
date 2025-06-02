package com.example.ms_descuentosclientefrecuente.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas") // This microservice will have its own 'reservas' table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inicio_reserva")
    private LocalDateTime inicioReserva;

    @Column(name = "fin_reserva")
    private LocalDateTime finReserva;

    @Column(name = "fecha")
    private LocalDate fecha; // Date of the reservation event

    @Column(name = "emailarrendatario")
    private String emailArrendatario;

    @Column(name = "duracion")
    private Integer duracion; // Assuming this is in some unit, e.g., hours or slots

    @Column(name = "numero_personas")
    private Integer numeroPersonas;

    @Column(name = "cumpleanios")
    private LocalDate cumpleanios; // Birthday of the person, if applicable

    @Column(name = "cantidadcumple")
    private Integer cantidadCumple; // Number of people celebrating birthday

    // Financial fields - these might not be directly used by this discount service
    // but are kept for structural consistency if data is replicated/synced
    @Column(name = "precio_inicial")
    private Double precioInicial;

    @Column(name = "descuento_grupo")
    private Double descuentoGrupo;

    @Column(name = "descuento_frecuente")
    private Double descuentoFrecuente; // This would be the result for past reservations

    @Column(name = "descuento_cumple")
    private Double descuentoCumple;

    @Column(name = "iva")
    private Double iva;

    @Column(name = "total_con_iva")
    private Double totalConIva;

    @Column(name = "tiporeserva")
    private Integer tipoReserva; // e.g., 1 for individual, 2 for group, 3 for birthday
}
