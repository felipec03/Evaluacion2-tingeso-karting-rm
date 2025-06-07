package com.example.ms_registroreserva_comprobantepago.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TarifaDTO {
    // Getters and Setters
    private Long id;
    private Integer tipoReserva;
    private String descripcion;
    private Double precioBasePorPersona;
    private Boolean activa;

    // Constructors
    public TarifaDTO() {
    }
    public TarifaDTO(Long id, Integer tipoReserva, String descripcion, Double precioBasePorPersona, Boolean activa) {
        this.id = id;
        this.tipoReserva = tipoReserva;
        this.descripcion = descripcion;
        this.precioBasePorPersona = precioBasePorPersona;
        this.activa = activa;
    }

}
