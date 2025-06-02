package com.example.ms_tarifasconfig.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "feriados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeriadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fecha; // Store as "MM-dd" for recurring holidays

    private String descripcion;
}