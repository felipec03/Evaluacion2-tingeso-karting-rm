package com.example.ms_registroreserva_comprobantepago.controllers;

import com.example.ms_registroreserva_comprobantepago.dtos.CrearReservaDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.ReservaConPagosDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.ReservaDTO;
import com.example.ms_registroreserva_comprobantepago.entities.EstadoReserva;
import com.example.ms_registroreserva_comprobantepago.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/")
    public ResponseEntity<ReservaDTO> registrarReserva(@Valid @RequestBody CrearReservaDTO crearReservaDTO) {
        ReservaDTO nuevaReserva = reservaService.registrarReserva(crearReservaDTO);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerReservaPorId(@PathVariable Long id) {
        ReservaDTO reserva = reservaService.obtenerReservaPorId(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasPorCliente(@PathVariable Long idCliente) {
        List<ReservaDTO> reservas = reservaService.obtenerReservasPorCliente(idCliente);
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ReservaDTO> actualizarEstadoReserva(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        // Espera un JSON como: { "estado": "CONFIRMADA" }
        String nuevoEstadoStr = payload.get("estado");
        if (nuevoEstadoStr == null) {
            return ResponseEntity.badRequest().build(); // O lanzar una excepción
        }
        try {
            EstadoReserva nuevoEstado = EstadoReserva.valueOf(nuevoEstadoStr.toUpperCase());
            ReservaDTO reservaActualizada = reservaService.actualizarEstadoReserva(id, nuevoEstado);
            return ResponseEntity.ok(reservaActualizada);
        } catch (IllegalArgumentException e) {
            // Manejar el caso de un valor de estado inválido
            return ResponseEntity.badRequest().body(null); // Considerar un DTO de error
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable Long id) {
        ReservaDTO reservaCancelada = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(reservaCancelada);
    }

    // Endpoint para el "Comprobante de Pago para Clientes"
    @GetMapping("/{idReserva}/comprobante-cliente")
    public ResponseEntity<ReservaConPagosDTO> generarComprobanteParaCliente(@PathVariable Long idReserva) {
        ReservaConPagosDTO comprobante = reservaService.obtenerReservaConPagos(idReserva);
        return ResponseEntity.ok(comprobante);
    }
}