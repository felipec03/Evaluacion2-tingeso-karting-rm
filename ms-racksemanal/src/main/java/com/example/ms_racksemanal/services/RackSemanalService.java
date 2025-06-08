package com.example.ms_racksemanal.services;

import com.example.ms_racksemanal.entities.RackSemanal;
import com.example.ms_racksemanal.feignclient.ReservaClient;
import com.example.ms_racksemanal.model.Reserva;
import com.example.ms_racksemanal.repositories.RackSemanalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RackSemanalService {

    @Autowired
    private RackSemanalRepository rackSemanalRepository;

    @Autowired
    private ReservaClient reservaClient;

    private static final String[] DIAS_SEMANA = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private static final String[] BLOQUES_TIEMPO = {
            "09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00",
            "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00",
            "17:00-18:00", "18:00-19:00", "19:00-20:00"
    };

    /**
     * Inicializa el rack semanal con todos los espacios disponibles
     */
    public void inicializarRackSemanal() {
        // Primero limpiamos la tabla
        rackSemanalRepository.deleteAll();

        // Creamos un espacio para cada combinación día-bloque
        for (String dia : DIAS_SEMANA) {
            for (String bloque : BLOQUES_TIEMPO) {
                RackSemanal rack = new RackSemanal(dia, bloque);
                rackSemanalRepository.save(rack);
            }
        }
        log.info("Rack semanal inicializado correctamente");
    }

    /**
     * Actualiza el rack semanal con las reservas existentes
     */

    public void actualizarRackSemanal() {
        // Reset all cells first
        List<RackSemanal> celdasRack = rackSemanalRepository.findAll();
        for (RackSemanal celda : celdasRack) {
            celda.setReservado(false);
            celda.setReservaId(null);
            rackSemanalRepository.save(celda);
        }

        // Get all reservations
        ResponseEntity<List<Reserva>> response = reservaClient.getAllReservas();
        List<Reserva> reservas = response.getBody();

        if (reservas == null || reservas.isEmpty()) {
            log.warn("No se encontraron reservas para actualizar el rack");
            return;
        }

        // Debug logging
        log.info("Obtenidas {} reservas para actualizar el rack", reservas.size());

        for (Reserva reserva : reservas) {
            if (reserva == null || "CANCELADA".equals(reserva.getEstadoReserva())) {
                continue;
            }

            if (reserva.getFechaHora() == null) {
                log.warn("Reserva ID {} tiene fechaHora nula", reserva.getId());
                continue;
            }

            try {
                // Extract date part from LocalDateTime
                LocalDate fecha = reserva.getFechaHora().toLocalDate();
                String diaSemana = obtenerNombreDiaSemana(fecha);

                // Calculate time block from fechaHora and duracionMinutos
                LocalDateTime inicio = reserva.getFechaHora();
                LocalDateTime fin = inicio.plusMinutes(reserva.getDuracionMinutos());

                String bloqueTiempo = String.format("%02d:%02d-%02d:%02d",
                        inicio.getHour(), inicio.getMinute(),
                        fin.getHour(), fin.getMinute());

                log.info("Procesando reserva ID {}: dia={}, bloque={}",
                        reserva.getId(), diaSemana, bloqueTiempo);

                // Find and update the cell
                RackSemanal celda = rackSemanalRepository.findByDiaSemanaAndBloqueTiempo(diaSemana, bloqueTiempo);
                if (celda != null) {
                    celda.setReservado(true);
                    celda.setReservaId(reserva.getId());
                    rackSemanalRepository.save(celda);
                    log.info("Celda actualizada: {}", celda);
                } else {
                    log.warn("No se encontró celda para día {} y bloque {}", diaSemana, bloqueTiempo);
                }
            } catch (Exception e) {
                log.error("Error procesando reserva ID {}: {}", reserva.getId(), e.getMessage(), e);
            }
        }
    }

    // Helper method to get day name in Spanish
    private String obtenerNombreDiaSemana(LocalDate fecha) {
        DayOfWeek dayOfWeek = fecha.getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    /**
     * Obtiene la matriz completa del rack semanal
     */
    public Map<String, Map<String, Boolean>> obtenerMatrizRackSemanal() {
        List<RackSemanal> todasCeldas = rackSemanalRepository.findAll();
        Map<String, Map<String, Boolean>> matriz = new LinkedHashMap<>();

        // Inicializamos la matriz con todos los días y bloques
        for (String dia : DIAS_SEMANA) {
            matriz.put(dia, new LinkedHashMap<>());
            for (String bloque : BLOQUES_TIEMPO) {
                matriz.get(dia).put(bloque, false);
            }
        }

        // Llenamos la matriz con los datos reales
        for (RackSemanal celda : todasCeldas) {
            String dia = celda.getDiaSemana();
            String bloque = celda.getBloqueTiempo();
            if (matriz.containsKey(dia) && matriz.get(dia).containsKey(bloque)) {
                matriz.get(dia).put(bloque, celda.isReservado());
            }
        }

        return matriz;
    }

    /**
     * Obtiene todas las celdas del rack semanal
     */
    public List<RackSemanal> obtenerTodasLasCeldas() {
        return rackSemanalRepository.findAll();
    }

    /**
     * Verifica si un horario específico está disponible
     */
    public boolean verificarDisponibilidad(String diaSemana, String bloqueTiempo) {
        RackSemanal celda = rackSemanalRepository.findByDiaSemanaAndBloqueTiempo(diaSemana, bloqueTiempo);
        return celda != null && !celda.isReservado();
    }

    /**
     * Obtiene los bloques disponibles para un día específico
     */
    public List<String> obtenerBloquesDisponiblesPorDia(String diaSemana) {
        List<RackSemanal> celdasDia = rackSemanalRepository.findByDiaSemana(diaSemana);
        List<String> bloquesDisponibles = new ArrayList<>();

        for (RackSemanal celda : celdasDia) {
            if (!celda.isReservado()) {
                bloquesDisponibles.add(celda.getBloqueTiempo());
            }
        }

        return bloquesDisponibles;
    }

    /**
     * Obtiene detalles de una reserva por su ID
     */
    public Reserva obtenerDetallesReserva(Long reservaId) {
        return reservaClient.getReservaById(reservaId).getBody();
    }

    /**
     * Obtiene todas las celdas reservadas
     */
    public List<RackSemanal> obtenerCeldasReservadas() {
        return rackSemanalRepository.findByReservado(true);
    }
}