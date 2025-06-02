package com.example.ms_tarifasconfig.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer tipoReserva; // e.g., 1 for Adulto, 2 for Niño, 3 for Mixta

    @Column(nullable = false)
    private String descripcion; // e.g., "Adulto (10 vueltas)", "Niño (10 vueltas)"

    @Column(nullable = false)
    private Float precioBasePorPersona; // Price per person for this type

    @Column(nullable = false)
    private Float porcentajeRecargoFinDeSemana = 0.0f; // e.g., 0.15 for 15%

    @Column(nullable = false)
    private Float porcentajeRecargoFeriado = 0.0f; // e.g., 0.25 for 25%

    private boolean activa = true;
}