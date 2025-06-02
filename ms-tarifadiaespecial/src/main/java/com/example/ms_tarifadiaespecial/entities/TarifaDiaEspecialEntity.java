package com.example.ms_tarifadiaespecial.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tarifas_dias_especiales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDiaEspecialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate fecha; // The special date

    @Column(nullable = false)
    private String descripcion; // e.g., "Navidad", "Año Nuevo"

    @Column(nullable = false)
    private String tipoTarifa; // "FIJA", "PORCENTUAL_DESCUENTO", "PORCENTUAL_RECARGO"

    @Column(nullable = false)
    private Double valor; // The fixed amount or percentage value
}