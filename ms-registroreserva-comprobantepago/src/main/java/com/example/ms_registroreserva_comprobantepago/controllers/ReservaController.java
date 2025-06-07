package com.example.ms_registroreserva_comprobantepago.controllers;

import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import com.example.ms_registroreserva_comprobantepago.services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaEntity>> getAllReservas() {
        List<ReservaEntity> reservas = reservaService.getAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaEntity> getReservaById(@PathVariable Long id) {
        return reservaService.getReservaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody ReservaEntity reserva) {
        try {
            ReservaEntity nuevaReserva = reservaService.crearReserva(reserva);
            return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoReserva(@PathVariable Long id, @RequestBody String nuevoEstado) {
        // El nuevoEstado vendría en el body como un simple string, ej: "CANCELADA"
        // Podrías usar un DTO si prefieres: ej. {"estado": "CANCELADA"}
        try {
            ReservaEntity reservaActualizada = reservaService.actualizarEstadoReserva(id, nuevoEstado.replace("\"", "")); // Quitar comillas si viene como JSON string
            return ResponseEntity.ok(reservaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        try {
            reservaService.deleteReserva(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}