package com.example.ms_tarifadiaespecial.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tarifas_dias_especiales")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TarifaDiaEspecialEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha; // Día feriado específico, puede ser null para fines de semana

    private String descripcion; // Ej: "Navidad", "Feriado", "Fin de semana"

    private Double recargoPorcentaje; // Ej: 25.0 para 25% de recargo, puede ser 0 para sin recargo

    private Boolean esFeriado; // true si es feriado, false si es fin de semana
}