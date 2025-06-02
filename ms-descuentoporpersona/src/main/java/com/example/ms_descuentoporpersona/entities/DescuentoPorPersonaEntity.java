package com.example.ms_descuentoporpersona.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "descuentos_por_persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoPorPersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer minPersonas;

    @Column(nullable = false)
    private Integer maxPersonas;

    @Column(nullable = false)
    private Float porcentajeDescuento; // e.g., 0.1 for 10%

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}