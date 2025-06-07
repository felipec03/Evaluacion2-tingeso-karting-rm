package com.example.ms_racksemanal.controllers;

import com.example.ms_racksemanal.entities.RackSemanal;
import com.example.ms_racksemanal.model.Reserva;
import com.example.ms_racksemanal.services.RackSemanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rack-semanal")
public class RackSemanalController {

    @Autowired
    private RackSemanalService rackSemanalService;

    @PostMapping("/inicializar")
    public ResponseEntity<String> inicializarRack() {
        rackSemanalService.inicializarRackSemanal();
        return ResponseEntity.ok("Rack semanal inicializado correctamente");
    }

    @PostMapping("/actualizar")
    public ResponseEntity<String> actualizarRack() {
        rackSemanalService.actualizarRackSemanal();
        return ResponseEntity.ok("Rack semanal actualizado con las reservas existentes");
    }

    @GetMapping("/matriz")
    public ResponseEntity<Map<String, Map<String, Boolean>>> obtenerMatrizRack() {
        Map<String, Map<String, Boolean>> matriz = rackSemanalService.obtenerMatrizRackSemanal();
        return ResponseEntity.ok(matriz);
    }

    @GetMapping
    public ResponseEntity<List<RackSemanal>> obtenerTodasLasCeldas() {
        List<RackSemanal> celdas = rackSemanalService.obtenerTodasLasCeldas();
        return ResponseEntity.ok(celdas);
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @RequestParam String diaSemana,
            @RequestParam String bloqueTiempo) {
        boolean disponible = rackSemanalService.verificarDisponibilidad(diaSemana, bloqueTiempo);
        return ResponseEntity.ok(disponible);
    }

    @GetMapping("/bloques-disponibles/{diaSemana}")
    public ResponseEntity<List<String>> obtenerBloquesDisponibles(@PathVariable String diaSemana) {
        List<String> bloques = rackSemanalService.obtenerBloquesDisponiblesPorDia(diaSemana);
        return ResponseEntity.ok(bloques);
    }

    @GetMapping("/reservados")
    public ResponseEntity<List<RackSemanal>> obtenerCeldasReservadas() {
        List<RackSemanal> celdasReservadas = rackSemanalService.obtenerCeldasReservadas();
        return ResponseEntity.ok(celdasReservadas);
    }

    @GetMapping("/detalles-reserva/{reservaId}")
    public ResponseEntity<Reserva> obtenerDetallesReserva(@PathVariable Long reservaId) {
        Reserva reserva = rackSemanalService.obtenerDetallesReserva(reservaId);
        if (reserva != null) {
            return ResponseEntity.ok(reserva);
        }
        return ResponseEntity.notFound().build();
    }
}
