package com.example.ms_tarifasconfig.services;

import com.example.ms_tarifasconfig.dtos.CalculoPrecioRequestDTO;
import com.example.ms_tarifasconfig.dtos.PrecioCalculadoDTO;
import com.example.ms_tarifasconfig.entities.TarifaEntity;
import com.example.ms_tarifasconfig.repositories.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private ConfiguracionService configuracionService;

    @Transactional(readOnly = true)
    public List<TarifaEntity> getAllTarifasActivas() {
        return tarifaRepository.findAllByActivaTrue();
    }
    @Transactional(readOnly = true)
    public List<TarifaEntity> getAllTarifas() {
        return tarifaRepository.findAll();
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
        Optional<TarifaEntity> existing = tarifaRepository.findByTipoReservaAndActivaTrue(tarifa.getTipoReserva());
        if(existing.isPresent() && (tarifa.getId() == null || !existing.get().getId().equals(tarifa.getId())) && tarifa.isActiva()){
            throw new IllegalArgumentException("Ya existe una tarifa activa para el tipo de reserva: " + tarifa.getTipoReserva());
        }
        tarifa.setId(null); // Ensure creation
        return tarifaRepository.save(tarifa);
    }

    @Transactional
    public TarifaEntity updateTarifa(Long id, TarifaEntity tarifaDetails) {
        TarifaEntity tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada con id: " + id));

        // Check for conflict if activating or changing tipoReserva of an active tariff
        if (tarifaDetails.isActiva()) {
            Optional<TarifaEntity> conflictingTariff = tarifaRepository.findByTipoReservaAndActivaTrue(tarifaDetails.getTipoReserva());
            if (conflictingTariff.isPresent() && !conflictingTariff.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otra tarifa activa para el tipo de reserva: " + tarifaDetails.getTipoReserva());
            }
        }

        tarifa.setTipoReserva(tarifaDetails.getTipoReserva());
        tarifa.setDescripcion(tarifaDetails.getDescripcion());
        tarifa.setPrecioBasePorPersona(tarifaDetails.getPrecioBasePorPersona());
        tarifa.setPorcentajeRecargoFinDeSemana(tarifaDetails.getPorcentajeRecargoFinDeSemana());
        tarifa.setPorcentajeRecargoFeriado(tarifaDetails.getPorcentajeRecargoFeriado());
        tarifa.setActiva(tarifaDetails.isActiva());
        return tarifaRepository.save(tarifa);
    }

    @Transactional
    public void deleteTarifa(Long id) {
        // Consider soft delete by setting activa = false instead, if business logic allows
        if (!tarifaRepository.existsById(id)) {
            throw new RuntimeException("Tarifa no encontrada con id: " + id);
        }
        tarifaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PrecioCalculadoDTO calcularPrecioBase(CalculoPrecioRequestDTO request) {
        TarifaEntity tarifa = tarifaRepository.findByTipoReservaAndActivaTrue(request.getTipoReserva())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró tarifa activa para el tipo de reserva: " + request.getTipoReserva()));

        if (request.getNumeroPersonas() == null || request.getNumeroPersonas() <= 0) {
            throw new IllegalArgumentException("El número de personas debe ser mayor a 0.");
        }
        if (request.getFechaHoraInicio() == null) {
            throw new IllegalArgumentException("La fecha y hora de inicio son obligatorias.");
        }


        float precioBasePorPersona = tarifa.getPrecioBasePorPersona();
        float precioTotalPersonas = precioBasePorPersona * request.getNumeroPersonas();

        LocalDateTime fechaHoraInicio = request.getFechaHoraInicio();
        LocalDate fechaInicio = fechaHoraInicio.toLocalDate();
        DayOfWeek diaSemana = fechaInicio.getDayOfWeek();

        float precioFinal = precioTotalPersonas;
        StringBuilder detalle = new StringBuilder(String.format("Precio base (%d personas): %.2f", request.getNumeroPersonas(), precioTotalPersonas));

        boolean esFeriado = configuracionService.esFeriado(fechaInicio);
        boolean esFinDeSemana = diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY;

        // Feriado surcharge takes precedence
        if (esFeriado && tarifa.getPorcentajeRecargoFeriado() != null && tarifa.getPorcentajeRecargoFeriado() > 0) {
            float recargo = precioTotalPersonas * tarifa.getPorcentajeRecargoFeriado();
            precioFinal += recargo;
            detalle.append(String.format(", Recargo Feriado (%.0f%%): +%.2f", tarifa.getPorcentajeRecargoFeriado() * 100, recargo));
        } else if (esFinDeSemana && tarifa.getPorcentajeRecargoFinDeSemana() != null && tarifa.getPorcentajeRecargoFinDeSemana() > 0) {
            float recargo = precioTotalPersonas * tarifa.getPorcentajeRecargoFinDeSemana();
            precioFinal += recargo;
            detalle.append(String.format(", Recargo Fin de Semana (%.0f%%): +%.2f", tarifa.getPorcentajeRecargoFinDeSemana() * 100, recargo));
        }

        return new PrecioCalculadoDTO(precioFinal, detalle.toString());
    }
}