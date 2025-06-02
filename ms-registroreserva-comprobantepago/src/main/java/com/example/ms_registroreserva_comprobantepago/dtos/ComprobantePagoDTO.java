package com.example.ms_registroreserva_comprobantepago.dtos;

import com.example.ms_registroreserva_comprobantepago.entities.EstadoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ComprobantePagoDTO {
    private Long id;
    private Long idReserva; // Para facilitar la referencia
    private BigDecimal montoPagado;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String numeroTransaccion;
    private EstadoPago estadoPago;
}
