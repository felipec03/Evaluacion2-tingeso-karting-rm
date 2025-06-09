package com.example.ms_tarifasconfig.controllers;

import com.example.ms_tarifasconfig.entities.TarifaEntity;
import com.example.ms_tarifasconfig.services.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@CrossOrigin("*")
public class TarifaController {
    @Autowired
    private TarifaService tarifaService;

    // Eliminar los métodos toDto y toEntity

    @GetMapping("/")
    public ResponseEntity<List<TarifaEntity>> getAllTarifas(@RequestParam(required = false, defaultValue = "false") boolean soloActivas) {
        List<TarifaEntity> tarifas = soloActivas ? tarifaService.getAllTarifasActivas() : tarifaService.getAllTarifas();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaEntity> getTarifaById(@PathVariable Long id) {
        return tarifaService.getTarifaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipoReserva}")
    public ResponseEntity<TarifaEntity> getTarifaByTipoReserva(@PathVariable Integer tipoReserva) {
        return tarifaService.getTarifaByTipoReserva(tipoReserva)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> createTarifa(@RequestBody TarifaEntity tarifa) {
        try {
            tarifa.setId(null); // Asegurar que es una creación
            TarifaEntity savedTarifa = tarifaService.saveTarifa(tarifa);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTarifa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Considerar loggear el error e.getMessage() o e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear tarifa: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTarifa(@PathVariable Long id, @RequestBody TarifaEntity tarifaDetails) {
        try {
            TarifaEntity updatedTarifa = tarifaService.updateTarifa(id, tarifaDetails);
            return ResponseEntity.ok(updatedTarifa);
        } catch (IllegalArgumentException e) { // Específico para validaciones como conflicto de tarifa activa
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) { // Captura "Tarifa no encontrada" u otros errores de runtime
            if (e.getMessage() != null && e.getMessage().contains("Tarifa no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            // Considerar loggear el error e.getMessage() o e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar tarifa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTarifa(@PathVariable Long id) {
        try {
            tarifaService.deleteTarifa(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Captura "Tarifa no encontrada"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}