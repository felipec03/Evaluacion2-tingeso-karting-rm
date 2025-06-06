package com.example.ms_tarifasconfig.services;

import com.example.ms_tarifasconfig.entities.TarifaEntity;
import com.example.ms_tarifasconfig.repositories.TarifaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TarifaService {
    @Autowired
    private TarifaRepository tarifaRepository;

    @PostConstruct
    public void initData() {
        if (tarifaRepository.count() == 0) {
            tarifaRepository.save(new TarifaEntity(1, "Normal - 10 vueltas o máx 10 min", 15000.0));
            tarifaRepository.save(new TarifaEntity(2, "Extendida - 15 vueltas o máx 15 min", 20000.0));
            tarifaRepository.save(new TarifaEntity(3, "Premium - 20 vueltas o máx 20 min", 25000.0));

            System.out.println("Datos iniciales de tarifas cargados.");
        }
    }

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
}