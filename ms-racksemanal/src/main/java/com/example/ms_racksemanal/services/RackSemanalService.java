package com.example.ms_racksemanal.services;

import com.example.ms_racksemanal.feignclient.ReservaClient;
import com.example.ms_racksemanal.model.Reserva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RackSemanalService {

    @Autowired
    private ReservaClient reservaClient;

    public List<Reserva> obtenerTodasLasReservasActivas() {
        try {
            ResponseEntity<List<Reserva>> response = reservaClient.getAllReservas();
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Reserva> todasLasReservas = response.getBody();
                List<Reserva> reservasActivas = todasLasReservas.stream()
                        .filter(reserva -> reserva.getEstadoReserva() != null &&
                                !reserva.getEstadoReserva().equalsIgnoreCase("CANCELADA") &&
                                reserva.getFechaHora() != null && // Ensure fechaHora is not null
                                reserva.getDuracionMinutos() != null) // Ensure duracionMinutos is not null
                        .collect(Collectors.toList());
                log.info("Obtenidas {} reservas activas de un total de {} desde ms-registroreserva-comprobantepago", reservasActivas.size(), todasLasReservas.size());
                return reservasActivas;
            }
            log.warn("No se pudieron obtener reservas o la respuesta fue vacía. Status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Error al obtener todas las reservas desde ms-registroreserva-comprobantepago: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}