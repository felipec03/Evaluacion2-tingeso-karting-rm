package com.example.ms_tarifasconfig.controllers;

import com.example.ms_tarifasconfig.dtos.ConfiguracionGeneralDTO;
import com.example.ms_tarifasconfig.dtos.FeriadoDTO;
import com.example.ms_tarifasconfig.entities.ConfiguracionGeneralEntity;
import com.example.ms_tarifasconfig.entities.FeriadoEntity;
import com.example.ms_tarifasconfig.services.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    // --- Mappers ---
    private ConfiguracionGeneralDTO toDto(ConfiguracionGeneralEntity entity) {
        if (entity == null) return null;
        return new ConfiguracionGeneralDTO(entity.getId(), entity.getDuracionMinimaHorasReserva(), entity.getDuracionMaximaHorasReserva(), entity.getIntervaloPermitidoHorasReserva());
    }

    private ConfiguracionGeneralEntity toEntity(ConfiguracionGeneralDTO dto) {
        if (dto == null) return null;
        ConfiguracionGeneralEntity entity = new ConfiguracionGeneralEntity();
        entity.setId(dto.getId()); // ID is managed by service for singleton-like config
        entity.setDuracionMinimaHorasReserva(dto.getDuracionMinimaHorasReserva());
        entity.setDuracionMaximaHorasReserva(dto.getDuracionMaximaHorasReserva());
        entity.setIntervaloPermitidoHorasReserva(dto.getIntervaloPermitidoHorasReserva());
        return entity;
    }

    private FeriadoDTO toDto(FeriadoEntity entity) {
        if (entity == null) return null;
        return new FeriadoDTO(entity.getId(), entity.getFecha(), entity.getDescripcion());
    }

    private FeriadoEntity toEntity(FeriadoDTO dto) {
        if (dto == null) return null;
        FeriadoEntity entity = new FeriadoEntity();
        entity.setId(dto.getId());
        entity.setFecha(dto.getFecha());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }

    // --- Endpoints ---
    @GetMapping("/general")
    public ResponseEntity<ConfiguracionGeneralDTO> getConfiguracionGeneral() {
        return configuracionService.getConfiguracionGeneral()
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); // Should always exist after first call
    }

    @PutMapping("/general")
    public ResponseEntity<?> updateConfiguracionGeneral(@RequestBody ConfiguracionGeneralDTO configDto) {
        try {
            ConfiguracionGeneralEntity configEntity = toEntity(configDto);
            ConfiguracionGeneralEntity updatedConfig = configuracionService.saveConfiguracionGeneral(configEntity);
            return ResponseEntity.ok(toDto(updatedConfig));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar configuración: " + e.getMessage());
        }
    }

    @GetMapping("/feriados")
    public ResponseEntity<List<FeriadoDTO>> getFeriados() {
        List<FeriadoDTO> feriados = configuracionService.getFeriados().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feriados);
    }

    @PostMapping("/feriados")
    public ResponseEntity<?> addFeriado(@RequestBody FeriadoDTO feriadoDto) {
        try {
            // Basic validation for "MM-dd" format
            if (feriadoDto.getFecha() == null || !feriadoDto.getFecha().matches("\\d{2}-\\d{2}")) {
                return ResponseEntity.badRequest().body("Formato de fecha para feriado debe ser MM-dd.");
            }
            FeriadoEntity feriado = toEntity(feriadoDto);
            FeriadoEntity savedFeriado = configuracionService.addFeriado(feriado);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(savedFeriado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar feriado: " + e.getMessage());
        }
    }

    @DeleteMapping("/feriados/{id}")
    public ResponseEntity<?> deleteFeriado(@PathVariable Long id) {
        try {
            configuracionService.deleteFeriado(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/validar-duracion")
    public ResponseEntity<Boolean> validarDuracion(@RequestParam int duracionHoras) {
        try {
            return ResponseEntity.ok(configuracionService.validarDuracion(duracionHoras));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

    @GetMapping("/es-feriado")
    public ResponseEntity<?> esFeriado(@RequestParam String fecha) { // fecha as "yyyy-MM-dd"
        try {
            LocalDate localDate = LocalDate.parse(fecha);
            return ResponseEntity.ok(configuracionService.esFeriado(localDate));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Usar yyyy-MM-dd.");
        }
    }
}
