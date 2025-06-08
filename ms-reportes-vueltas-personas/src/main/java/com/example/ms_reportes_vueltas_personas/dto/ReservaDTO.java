package com.example.ms_reportes_vueltas_personas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id; // O el tipo de ID que use
    private LocalDate fechaReserva;
    private String tipoTarifa; // Ej: "10 Vueltas", "15 Vueltas", "30 Minutos"
    private int numeroPersonas;
    private double montoTotal; // Ingreso de esta reserva
    // Otros campos que puedan venir y no necesitemos para los reportes
}