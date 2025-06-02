package com.example.ms_registroreserva_comprobantepago.controllers;

import com.example.ms_registroreserva_comprobantepago.dtos.ComprobantePagoDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.CrearPagoDTO;
import com.example.ms_registroreserva_comprobantepago.services.ComprobantePagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class ComprobantePagoController {

    @Autowired
    private ComprobantePagoService comprobantePagoService;

    @PostMapping("/")
    public ResponseEntity<ComprobantePagoDTO> registrarPago(@Valid @RequestBody CrearPagoDTO crearPagoDTO) {
        ComprobantePagoDTO nuevoPago = comprobantePagoService.registrarPago(crearPagoDTO);
        return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComprobantePagoDTO> obtenerComprobantePorId(@PathVariable Long id) {
        ComprobantePagoDTO comprobante = comprobantePagoService.obtenerComprobantePorId(id);
        return ResponseEntity.ok(comprobante);
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<ComprobantePagoDTO>> obtenerComprobantesPorReserva(@PathVariable Long idReserva) {
        List<ComprobantePagoDTO> comprobantes = comprobantePagoService.obtenerComprobantesPorReserva(idReserva);
        return ResponseEntity.ok(comprobantes);
    }
}