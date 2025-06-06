package com.example.ms_descuentoporpersona.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "descuento_persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoPersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "personas_min")
    private int personasMin;

    @Column(name = "personas_max")
    private int personasMax;

    @Column(name = "porcentaje_descuento")
    private double porcentajeDescuento;

    @Column(name = "activo")
    private boolean activo = true;
}