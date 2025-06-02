package com.example.ms_tarifasconfig.controllers;

import com.example.ms_tarifasconfig.dtos.CalculoPrecioRequestDTO;
import com.example.ms_tarifasconfig.dtos.PrecioCalculadoDTO;
import com.example.ms_tarifasconfig.dtos.TarifaDTO;
import com.example.ms_tarifasconfig.entities.TarifaEntity;
import com.example.ms_tarifasconfig.services.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    @Autowired
    private TarifaService tarifaService;

    // --- Mappers ---
    private TarifaDTO toDto(TarifaEntity entity) {
        if (entity == null) return null;
        return new TarifaDTO(entity.getId(), entity.getTipoReserva(), entity.getDescripcion(), entity.getPrecioBasePorPersona(), entity.getPorcentajeRecargoFinDeSemana(), entity.getPorcentajeRecargoFeriado(), entity.isActiva());
    }

    private TarifaEntity toEntity(TarifaDTO dto) {
        if (dto == null) return null;
        TarifaEntity entity = new TarifaEntity();
        entity.setId(dto.getId());
        entity.setTipoReserva(dto.getTipoReserva());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecioBasePorPersona(dto.getPrecioBasePorPersona());
        entity.setPorcentajeRecargoFinDeSemana(dto.getPorcentajeRecargoFinDeSemana());
        entity.setPorcentajeRecargoFeriado(dto.getPorcentajeRecargoFeriado());
        entity.setActiva(dto.isActiva());
        return entity;
    }

    // --- Endpoints ---
    @GetMapping("/")
    public ResponseEntity<List<TarifaDTO>> getAllTarifas(@RequestParam(required = false, defaultValue = "false") boolean soloActivas) {
        List<TarifaEntity> tarifas = soloActivas ? tarifaService.getAllTarifasActivas() : tarifaService.getAllTarifas();
        return ResponseEntity.ok(tarifas.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO> getTarifaById(@PathVariable Long id) {
        return tarifaService.getTarifaById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipoReserva}")
    public ResponseEntity<TarifaDTO> getTarifaByTipoReserva(@PathVariable Integer tipoReserva) {
        return tarifaService.getTarifaByTipoReserva(tipoReserva) // This gets active ones by default
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> createTarifa(@RequestBody TarifaDTO tarifaDto) {
        try {
            TarifaEntity tarifa = toEntity(tarifaDto);
            TarifaEntity savedTarifa = tarifaService.saveTarifa(tarifa);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(savedTarifa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear tarifa: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTarifa(@PathVariable Long id, @RequestBody TarifaDTO tarifaDto) {
        try {
            TarifaEntity tarifaDetails = toEntity(tarifaDto);
            TarifaEntity updatedTarifa = tarifaService.updateTarifa(id, tarifaDetails);
            return ResponseEntity.ok(toDto(updatedTarifa));
        } catch (RuntimeException e) { // Catches both RuntimeException from service and others
            if (e.getMessage() != null && e.getMessage().contains("Tarifa no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar tarifa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTarifa(@PathVariable Long id) {
        try {
            tarifaService.deleteTarifa(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/calcular-precio-base")
    public ResponseEntity<?> calcularPrecioBase(@RequestBody CalculoPrecioRequestDTO request) {
        try {
            PrecioCalculadoDTO precioCalculado = tarifaService.calcularPrecioBase(request);
            return ResponseEntity.ok(precioCalculado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al calcular el precio base: " + e.getMessage());
        }
    }
}