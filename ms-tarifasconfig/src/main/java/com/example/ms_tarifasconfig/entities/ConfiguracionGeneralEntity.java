package com.example.ms_tarifasconfig.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "configuracion_general")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionGeneralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Should ideally be a single record

    private int duracionMinimaHorasReserva = 1;
    private int duracionMaximaHorasReserva = 4;
    private int intervaloPermitidoHorasReserva = 1; // e.g., reservations can be 1h, 2h, 3h blocks
}