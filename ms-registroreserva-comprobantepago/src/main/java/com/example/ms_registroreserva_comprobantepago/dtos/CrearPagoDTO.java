package com.example.ms_registroreserva_comprobantepago.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearPagoDTO {
    @NotNull(message = "ID de reserva no puede ser nulo")
    private Long idReserva;

    @NotNull(message = "Monto a pagar no puede ser nulo")
    @Min(value = 0, message = "Monto a pagar no puede ser negativo")
    private BigDecimal montoPagado;

    @NotEmpty(message = "Método de pago no puede estar vacío")
    private String metodoPago;

    private String numeroTransaccion; // Opcional
}
