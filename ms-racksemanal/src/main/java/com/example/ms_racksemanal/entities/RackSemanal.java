package com.example.ms_racksemanal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rack_semanal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RackSemanal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diaSemana;    // Lunes, Martes, etc.
    private String bloqueTiempo; // Por ejemplo: "10:00-11:00"
    private boolean reservado;
    private Long reservaId;      // ID de la reserva asociada (si está reservado)

    // Constructor adicional para facilitar la creación
    public RackSemanal(String diaSemana, String bloqueTiempo) {
        this.diaSemana = diaSemana;
        this.bloqueTiempo = bloqueTiempo;
        this.reservado = false;
        this.reservaId = null;
    }
}
