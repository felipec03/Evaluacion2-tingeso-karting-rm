// filepath: ms-descuentoporpersona/src/main/java/com/example/ms_descuentoporpersona/services/DescuentoPorPersonaService.java
package com.example.ms_descuentoporpersona.services;

import com.example.ms_descuentoporpersona.dtos.CalculoDescuentoRequestDTO;
import com.example.ms_descuentoporpersona.dtos.DescuentoCalculadoDTO;
import com.example.ms_descuentoporpersona.entities.DescuentoPorPersonaEntity;
import com.example.ms_descuentoporpersona.repositories.DescuentoPorPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DescuentoPorPersonaService {

    @Autowired
    private DescuentoPorPersonaRepository descuentoRepository;

    @Transactional(readOnly = true)
    public List<DescuentoPorPersonaEntity> getAllDescuentos() {
        return descuentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DescuentoPorPersonaEntity> getAllDescuentosActivos() {
        return descuentoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<DescuentoPorPersonaEntity> getDescuentoById(Long id) {
        return descuentoRepository.findById(id);
    }

    @Transactional
    public DescuentoPorPersonaEntity createDescuento(DescuentoPorPersonaEntity descuento) {
        descuento.setId(null); // Ensure creation
        return descuentoRepository.save(descuento);
    }

    @Transactional
    public Optional<DescuentoPorPersonaEntity> updateDescuento(Long id, DescuentoPorPersonaEntity descuentoDetails) {
        return descuentoRepository.findById(id).map(existingDescuento -> {
            existingDescuento.setMinPersonas(descuentoDetails.getMinPersonas());
            existingDescuento.setMaxPersonas(descuentoDetails.getMaxPersonas());
            existingDescuento.setPorcentajeDescuento(descuentoDetails.getPorcentajeDescuento());
            existingDescuento.setDescripcion(descuentoDetails.getDescripcion());
            existingDescuento.setActivo(descuentoDetails.getActivo());
            return descuentoRepository.save(existingDescuento);
        });
    }

    @Transactional
    public boolean deleteDescuento(Long id) {
        if (descuentoRepository.existsById(id)) {
            descuentoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public DescuentoCalculadoDTO calcularDescuento(CalculoDescuentoRequestDTO request) {
        if (request.getNumeroPersonas() == null || request.getNumeroPersonas() <= 0) {
            throw new IllegalArgumentException("El número de personas debe ser mayor a 0.");
        }
        if (request.getPrecioInicial() == null || request.getPrecioInicial() < 0) {
            throw new IllegalArgumentException("El precio inicial no puede ser negativo.");
        }

        List<DescuentoPorPersonaEntity> aplicables = descuentoRepository.findMejorDescuentoAplicable(request.getNumeroPersonas());

        if (aplicables.isEmpty()) {
            return new DescuentoCalculadoDTO(0f, 0f, "No se aplicó descuento por número de personas.");
        }

        // Use the first one, which should be the best due to ORDER BY
        DescuentoPorPersonaEntity mejorDescuento = aplicables.get(0);
        float montoDescuento = request.getPrecioInicial() * mejorDescuento.getPorcentajeDescuento();
        String detalle = String.format("Descuento del %.2f%% aplicado (%s).",
                mejorDescuento.getPorcentajeDescuento() * 100,
                mejorDescuento.getDescripcion() != null ? mejorDescuento.getDescripcion() : "Regla ID: " + mejorDescuento.getId());

        return new DescuentoCalculadoDTO(montoDescuento, mejorDescuento.getPorcentajeDescuento(), detalle);
    }
}