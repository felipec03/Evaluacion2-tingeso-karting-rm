package com.example.ms_tarifasconfig.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer tipoReserva; // 1: Normal (10 vueltas), 2: Extendida (15 vueltas), 3: Premium (20 vueltas)

    @Column(nullable = false)
    private String descripcion; // e.g., "Normal (10 vueltas)", "Extendida (15 vueltas)"

    @Column(nullable = false)
    private Double precioBasePorPersona; // Precio base por persona para este tipo

    @Column(nullable = false)
    private Boolean activa = true;
}