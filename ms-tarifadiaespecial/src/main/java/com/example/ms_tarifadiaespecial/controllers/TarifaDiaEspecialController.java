package com.example.ms_tarifadiaespecial.controllers;
import com.example.ms_tarifadiaespecial.dtos.TarifaDiaEspecialDTO;
import com.example.ms_tarifadiaespecial.services.TarifaDiaEspecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarifas-dias-especiales")
public class TarifaDiaEspecialController {

    @Autowired
    private TarifaDiaEspecialService tarifaDiaEspecialService;

    @GetMapping("/")
    public ResponseEntity<List<TarifaDiaEspecialDTO>> getAllTarifas() {
        List<TarifaDiaEspecialDTO> tarifas = tarifaDiaEspecialService.getAllTarifas();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDiaEspecialDTO> getTarifaById(@PathVariable Long id) {
        return tarifaDiaEspecialService.getTarifaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<TarifaDiaEspecialDTO> getTarifaByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return tarifaDiaEspecialService.getTarifaByFecha(fecha)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> createTarifa(@RequestBody TarifaDiaEspecialDTO tarifaDTO) {
        try {
            TarifaDiaEspecialDTO createdTarifa = tarifaDiaEspecialService.createTarifa(tarifaDTO);
            return new ResponseEntity<>(createdTarifa, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTarifa(@PathVariable Long id, @RequestBody TarifaDiaEspecialDTO tarifaDTO) {
        try {
            return tarifaDiaEspecialService.updateTarifa(id, tarifaDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarifa(@PathVariable Long id) {
        if (tarifaDiaEspecialService.deleteTarifa(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/aplicar")
    public ResponseEntity<Double> aplicarTarifa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Double precioOriginal) {
        Double precioAjustado = tarifaDiaEspecialService.calcularPrecioConTarifaEspecial(fecha, precioOriginal);
        return ResponseEntity.ok(precioAjustado);
    }
}
