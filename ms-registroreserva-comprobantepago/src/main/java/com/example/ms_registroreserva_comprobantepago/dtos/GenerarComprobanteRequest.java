package com.example.ms_registroreserva_comprobantepago.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerarComprobanteRequest {
    @NotBlank(message = "El campo 'metodoPago' es requerido.")
    private String metodoPago;
}