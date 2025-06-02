package com.example.ms_descuentoporpersona.controllers;

import com.example.ms_descuentoporpersona.dtos.CalculoDescuentoRequestDTO;
import com.example.ms_descuentoporpersona.dtos.DescuentoCalculadoDTO;
import com.example.ms_descuentoporpersona.dtos.DescuentoPorPersonaDTO;
import com.example.ms_descuentoporpersona.entities.DescuentoPorPersonaEntity;
import com.example.ms_descuentoporpersona.services.DescuentoPorPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/descuentos-persona")
public class DescuentoPorPersonaController {

    @Autowired
    private DescuentoPorPersonaService descuentoService;

    // --- Mappers ---
    private DescuentoPorPersonaDTO toDto(DescuentoPorPersonaEntity entity) {
        if (entity == null) return null;
        return new DescuentoPorPersonaDTO(
                entity.getId(),
                entity.getMinPersonas(),
                entity.getMaxPersonas(),
                entity.getPorcentajeDescuento(),
                entity.getDescripcion(),
                entity.getActivo()
        );
    }

    private DescuentoPorPersonaEntity toEntity(DescuentoPorPersonaDTO dto) {
        if (dto == null) return null;
        DescuentoPorPersonaEntity entity = new DescuentoPorPersonaEntity();
        entity.setId(dto.getId());
        entity.setMinPersonas(dto.getMinPersonas());
        entity.setMaxPersonas(dto.getMaxPersonas());
        entity.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        entity.setDescripcion(dto.getDescripcion());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true); // Default to active if not specified
        return entity;
    }

    // --- Endpoints ---
    @GetMapping("/")
    public ResponseEntity<List<DescuentoPorPersonaDTO>> getAllDescuentos(@RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        List<DescuentoPorPersonaEntity> descuentos = soloActivos ?
                descuentoService.getAllDescuentosActivos() :
                descuentoService.getAllDescuentos();
        return ResponseEntity.ok(descuentos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescuentoPorPersonaDTO> getDescuentoById(@PathVariable Long id) {
        return descuentoService.getDescuentoById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<DescuentoPorPersonaDTO> createDescuento(@RequestBody DescuentoPorPersonaDTO dto) {
        try {
            DescuentoPorPersonaEntity entity = toEntity(dto);
            DescuentoPorPersonaEntity savedEntity = descuentoService.createDescuento(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(savedEntity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DescuentoPorPersonaDTO> updateDescuento(@PathVariable Long id, @RequestBody DescuentoPorPersonaDTO dto) {
        DescuentoPorPersonaEntity entityDetails = toEntity(dto);
        return descuentoService.updateDescuento(id, entityDetails)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDescuento(@PathVariable Long id) {
        if (descuentoService.deleteDescuento(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/calcular")
    public ResponseEntity<DescuentoCalculadoDTO> calcularDescuento(@RequestBody CalculoDescuentoRequestDTO request) {
        try {
            DescuentoCalculadoDTO resultado = descuentoService.calcularDescuento(request);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new DescuentoCalculadoDTO(null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DescuentoCalculadoDTO(null, null, "Error al calcular descuento: " + e.getMessage()));
        }
    }
}