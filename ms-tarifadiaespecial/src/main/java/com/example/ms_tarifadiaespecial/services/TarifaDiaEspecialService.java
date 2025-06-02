package com.example.ms_tarifadiaespecial.services;

import com.example.ms_tarifadiaespecial.dtos.TarifaDiaEspecialDTO;
import com.example.ms_tarifadiaespecial.entities.TarifaDiaEspecialEntity;
import com.example.ms_tarifadiaespecial.repositories.TarifaDiaEspecialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TarifaDiaEspecialService {

    @Autowired
    private TarifaDiaEspecialRepository tarifaDiaEspecialRepository;

    // Mapper DTO to Entity
    private TarifaDiaEspecialEntity toEntity(TarifaDiaEspecialDTO dto) {
        TarifaDiaEspecialEntity entity = new TarifaDiaEspecialEntity();
        entity.setId(dto.getId());
        entity.setFecha(dto.getFecha());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipoTarifa(dto.getTipoTarifa());
        entity.setValor(dto.getValor());
        return entity;
    }

    // Mapper Entity to DTO
    private TarifaDiaEspecialDTO toDTO(TarifaDiaEspecialEntity entity) {
        TarifaDiaEspecialDTO dto = new TarifaDiaEspecialDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        dto.setDescripcion(entity.getDescripcion());
        dto.setTipoTarifa(entity.getTipoTarifa());
        dto.setValor(entity.getValor());
        return dto;
    }

    public List<TarifaDiaEspecialDTO> getAllTarifas() {
        return tarifaDiaEspecialRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TarifaDiaEspecialDTO> getTarifaById(Long id) {
        return tarifaDiaEspecialRepository.findById(id).map(this::toDTO);
    }

    public Optional<TarifaDiaEspecialDTO> getTarifaByFecha(LocalDate fecha) {
        return tarifaDiaEspecialRepository.findByFecha(fecha).map(this::toDTO);
    }

    public TarifaDiaEspecialDTO createTarifa(TarifaDiaEspecialDTO tarifaDTO) {
        if (tarifaDiaEspecialRepository.existsByFecha(tarifaDTO.getFecha())) {
            throw new IllegalArgumentException("Ya existe una tarifa para la fecha: " + tarifaDTO.getFecha());
        }
        TarifaDiaEspecialEntity entity = toEntity(tarifaDTO);
        // Ensure ID is null for creation to let DB generate it
        entity.setId(null);
        return toDTO(tarifaDiaEspecialRepository.save(entity));
    }

    public Optional<TarifaDiaEspecialDTO> updateTarifa(Long id, TarifaDiaEspecialDTO tarifaDTO) {
        return tarifaDiaEspecialRepository.findById(id)
                .map(existingTarifa -> {
                    // Check if updating fecha to one that already exists (and isn't the current one)
                    if (!existingTarifa.getFecha().equals(tarifaDTO.getFecha()) &&
                            tarifaDiaEspecialRepository.existsByFecha(tarifaDTO.getFecha())) {
                        throw new IllegalArgumentException("Ya existe otra tarifa para la fecha: " + tarifaDTO.getFecha());
                    }
                    existingTarifa.setFecha(tarifaDTO.getFecha());
                    existingTarifa.setDescripcion(tarifaDTO.getDescripcion());
                    existingTarifa.setTipoTarifa(tarifaDTO.getTipoTarifa());
                    existingTarifa.setValor(tarifaDTO.getValor());
                    return toDTO(tarifaDiaEspecialRepository.save(existingTarifa));
                });
    }

    public boolean deleteTarifa(Long id) {
        if (tarifaDiaEspecialRepository.existsById(id)) {
            tarifaDiaEspecialRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Double calcularPrecioConTarifaEspecial(LocalDate fecha, Double precioOriginal) {
        Optional<TarifaDiaEspecialEntity> tarifaOpt = tarifaDiaEspecialRepository.findByFecha(fecha);
        if (tarifaOpt.isPresent()) {
            TarifaDiaEspecialEntity tarifa = tarifaOpt.get();
            switch (tarifa.getTipoTarifa().toUpperCase()) {
                case "FIJA":
                    return tarifa.getValor();
                case "PORCENTUAL_DESCUENTO":
                    return precioOriginal * (1 - (tarifa.getValor() / 100.0));
                case "PORCENTUAL_RECARGO":
                    return precioOriginal * (1 + (tarifa.getValor() / 100.0));
                default:
                    // Should not happen if data is validated, but good to have a fallback
                    return precioOriginal;
            }
        }
        return precioOriginal; // No special tariff for this day
    }
}