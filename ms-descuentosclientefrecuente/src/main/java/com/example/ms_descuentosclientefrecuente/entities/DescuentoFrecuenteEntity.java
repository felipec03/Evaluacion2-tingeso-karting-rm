package com.example.ms_descuentosclientefrecuente.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "descuentos-frecuente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoFrecuenteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rutCliente;
    private Integer mes;
    private Integer anio;
    private Integer cantidadVisitas;
    private Double porcentajeDescuento;
}