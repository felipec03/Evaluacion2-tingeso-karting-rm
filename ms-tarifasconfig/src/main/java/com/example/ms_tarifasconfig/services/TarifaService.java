package com.example.ms_tarifasconfig.services;

import com.example.ms_tarifasconfig.dtos.CalculoPrecioRequestDTO;
import com.example.ms_tarifasconfig.dtos.PrecioCalculadoDTO;
import com.example.ms_tarifasconfig.entities.TarifaEntity;
import com.example.ms_tarifasconfig.repositories.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    @Transactional(readOnly = true)
    public List<TarifaEntity> getAllTarifas() {
        return tarifaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TarifaEntity> getAllTarifasActivas() {
        return tarifaRepository.findAllByActivaTrue();
    }

    @Transactional(readOnly = true)
    public Optional<TarifaEntity> getTarifaById(Long id) {
        return tarifaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<TarifaEntity> getTarifaByTipoReserva(Integer tipoReserva) {
        return tarifaRepository.findByTipoReservaAndActivaTrue(tipoReserva);
    }

    @Transactional
    public TarifaEntity saveTarifa(TarifaEntity tarifa) {
        // Validar que no exista otra tarifa activa para el mismo tipo
        Optional<TarifaEntity> existing = tarifaRepository.findByTipoReservaAndActivaTrue(tarifa.getTipoReserva());
        if (existing.isPresent() && (tarifa.getId() == null || !existing.get().getId().equals(tarifa.getId())) && tarifa.getActiva()) {
            throw new IllegalArgumentException("Ya existe una tarifa activa para el tipo de reserva: " + tarifa.getTipoReserva());
        }
        return tarifaRepository.save(tarifa);
    }

    @Transactional
    public TarifaEntity updateTarifa(Long id, TarifaEntity tarifaDetails) {
        TarifaEntity tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con id: " + id));

        // Validar conflictos si se está activando
        if (tarifaDetails.getActiva()) {
            Optional<TarifaEntity> conflictingTariff = tarifaRepository.findByTipoReservaAndActivaTrue(tarifaDetails.getTipoReserva());
            if (conflictingTariff.isPresent() && !conflictingTariff.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otra tarifa activa para el tipo de reserva: " + tarifaDetails.getTipoReserva());
            }
        }

        tarifa.setTipoReserva(tarifaDetails.getTipoReserva());
        tarifa.setDescripcion(tarifaDetails.getDescripcion());
        tarifa.setPrecioBasePorPersona(tarifaDetails.getPrecioBasePorPersona());
        tarifa.setActiva(tarifaDetails.getActiva());

        return tarifaRepository.save(tarifa);
    }

    @Transactional
    public void deleteTarifa(Long id) {
        if (!tarifaRepository.existsById(id)) {
            throw new RuntimeException("Tarifa no encontrada con id: " + id);
        }
        tarifaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PrecioCalculadoDTO calcularPrecioBase(CalculoPrecioRequestDTO request) {
        // Validar entrada
        if (request.getNumeroPersonas() == null || request.getNumeroPersonas() <= 0) {
            throw new IllegalArgumentException("El número de personas debe ser mayor a 0.");
        }
        if (request.getTipoReserva() == null) {
            throw new IllegalArgumentException("El tipo de reserva es obligatorio.");
        }

        // Buscar tarifa activa
        TarifaEntity tarifa = tarifaRepository.findByTipoReservaAndActivaTrue(request.getTipoReserva())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró tarifa activa para el tipo de reserva: " + request.getTipoReserva()));

        // Calcular precio base
        double precioBasePorPersona = tarifa.getPrecioBasePorPersona();
        double precioTotal = precioBasePorPersona * request.getNumeroPersonas();

        String detalle = String.format("Tipo: %s, Precio base por persona: $%.2f, Personas: %d",
                tarifa.getDescripcion(), precioBasePorPersona, request.getNumeroPersonas());

        return new PrecioCalculadoDTO(precioTotal, detalle);
    }
}