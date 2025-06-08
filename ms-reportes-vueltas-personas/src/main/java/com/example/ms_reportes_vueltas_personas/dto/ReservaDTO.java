package com.example.ms_reportes_vueltas_personas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private LocalDateTime fechaHora;        // Corresponds to ReservaEntity.fechaHora
    private int tipoReserva;                // Corresponds to ReservaEntity.tipoReserva
    private int cantidadPersonas;           // Corresponds to ReservaEntity.cantidadPersonas
    // private int cantidadCumple;          // Available in Entity, can be added if future reports need it
    // private String nombreUsuario;        // Available, not used in current reports
    // private String rutUsuario;           // Available, not used
    // private String emailUsuario;         // Available, not used
    // private String telefonoUsuario;      // Available, not used
    // private Double montoBase;            // Available, not used directly for income sum
    // private Double porcentajeDescuentoAplicado; // Available, not used
    // private Double montoDescuento;       // Available, not used
    private Double montoFinal;              // Corresponds to ReservaEntity.montoFinal (use Double for null safety)
    private String estadoReserva;           // Corresponds to ReservaEntity.estadoReserva
    private Integer duracionMinutos;        // Corresponds to ReservaEntity.duracionMinutos (use Integer for null safety)
}