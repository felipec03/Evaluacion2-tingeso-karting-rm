package com.example.ms_tarifadiaespecial.controllers;

import com.example.ms_tarifadiaespecial.entities.TarifaDiaEspecialEntity;
import com.example.ms_tarifadiaespecial.services.TarifaDiaEspecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarifas-dias-especiales")
@CrossOrigin("*")
public class TarifaDiaEspecialController {

    @Autowired
    private TarifaDiaEspecialService service;

    @GetMapping("/")
    public List<TarifaDiaEspecialEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDiaEspecialEntity> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public TarifaDiaEspecialEntity create(@RequestBody TarifaDiaEspecialEntity entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aplicar")
    public ResponseEntity<Double> aplicarTarifa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Double precioBase,
            @RequestParam int cantidadPersonas,
            @RequestParam int cumpleanieros) {
        Double precioFinal = service.aplicarTarifaEspecial(fecha, precioBase, cantidadPersonas, cumpleanieros);
        return ResponseEntity.ok(precioFinal);
    }
}
