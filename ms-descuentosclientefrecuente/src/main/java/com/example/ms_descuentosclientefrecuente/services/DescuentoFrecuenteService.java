package com.example.ms_descuentosclientefrecuente.services;

import com.example.ms_descuentosclientefrecuente.dtos.DescuentoFrecuenteRequestDto;
import com.example.ms_descuentosclientefrecuente.dtos.DescuentoFrecuenteResponseDto;
import com.example.ms_descuentosclientefrecuente.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DescuentoFrecuenteService {

    @Autowired
    private ReservaRepository reservaRepository;

    private static final int UMBRAL_RESERVAS_FRECUENTES = 3;
    private static final double PORCENTAJE_DESCUENTO_FRECUENTE = 0.05; // 5%

    public DescuentoFrecuenteResponseDto calcularDescuento(DescuentoFrecuenteRequestDto request) {
        LocalDate fechaReservaActual = request.getFechaReservaActual();
        String email = request.getEmailArrendatario();

        // Define the 12-month period prior to the current reservation
        LocalDate fechaFinPeriodo = fechaReservaActual.minusDays(1); // Up to the day before the current reservation
        LocalDate fechaInicioPeriodo = fechaReservaActual.minusYears(1);

        long countReservasPrevias = 0;
        if (fechaInicioPeriodo.isBefore(fechaFinPeriodo.plusDays(1))) { // Ensure start is before or same as end
            countReservasPrevias = reservaRepository.countByEmailArrendatarioAndFechaBetween(
                    email,
                    fechaInicioPeriodo,
                    fechaFinPeriodo
            );
        }

        double descuentoAplicado = 0.0;
        boolean esFrecuente = false;

        if (countReservasPrevias >= UMBRAL_RESERVAS_FRECUENTES) {
            descuentoAplicado = request.getPrecioInicial() * PORCENTAJE_DESCUENTO_FRECUENTE;
            esFrecuente = true;
        }

        return new DescuentoFrecuenteResponseDto(
                descuentoAplicado,
                esFrecuente,
                email,
                (int) countReservasPrevias
        );
    }
}
