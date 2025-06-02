package com.example.ms_tarifasconfig.services;

import com.example.ms_tarifasconfig.entities.ConfiguracionGeneralEntity;
import com.example.ms_tarifasconfig.entities.FeriadoEntity;
import com.example.ms_tarifasconfig.repositories.ConfiguracionGeneralRepository;
import com.example.ms_tarifasconfig.repositories.FeriadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionGeneralRepository configuracionGeneralRepository;

    @Autowired
    private FeriadoRepository feriadoRepository;

    private static final DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Transactional(readOnly = true)
    public Optional<ConfiguracionGeneralEntity> getConfiguracionGeneral() {
        Optional<ConfiguracionGeneralEntity> configOpt = configuracionGeneralRepository.findFirst();
        if (configOpt.isEmpty()) {
            ConfiguracionGeneralEntity defaultConfig = new ConfiguracionGeneralEntity();
            // Set default values if necessary upon first creation
            // defaultConfig.setDuracionMinimaHorasReserva(1);
            // defaultConfig.setDuracionMaximaHorasReserva(4);
            // defaultConfig.setIntervaloPermitidoHorasReserva(1);
            return Optional.of(configuracionGeneralRepository.save(defaultConfig));
        }
        return configOpt;
    }

    @Transactional
    public ConfiguracionGeneralEntity saveConfiguracionGeneral(ConfiguracionGeneralEntity config) {
        Optional<ConfiguracionGeneralEntity> existingConfigOpt = configuracionGeneralRepository.findFirst();
        if (existingConfigOpt.isPresent()) {
            ConfiguracionGeneralEntity existingConfig = existingConfigOpt.get();
            existingConfig.setDuracionMinimaHorasReserva(config.getDuracionMinimaHorasReserva());
            existingConfig.setDuracionMaximaHorasReserva(config.getDuracionMaximaHorasReserva());
            existingConfig.setIntervaloPermitidoHorasReserva(config.getIntervaloPermitidoHorasReserva());
            return configuracionGeneralRepository.save(existingConfig);
        }
        // If no config exists, save the new one. ID will be generated.
        config.setId(null);
        return configuracionGeneralRepository.save(config);
    }

    @Transactional(readOnly = true)
    public boolean esFeriado(LocalDate fecha) {
        String fechaMesDia = fecha.format(MONTH_DAY_FORMATTER);
        return feriadoRepository.findByFecha(fechaMesDia).isPresent();
    }

    @Transactional(readOnly = true)
    public List<FeriadoEntity> getFeriados() {
        return feriadoRepository.findAll();
    }

    @Transactional
    public FeriadoEntity addFeriado(FeriadoEntity feriado) {
        if(feriadoRepository.findByFecha(feriado.getFecha()).isPresent()){
            throw new IllegalArgumentException("Feriado con fecha " + feriado.getFecha() + " ya existe.");
        }
        feriado.setId(null); // Ensure creation
        return feriadoRepository.save(feriado);
    }

    @Transactional
    public void deleteFeriado(Long id) {
        if (!feriadoRepository.existsById(id)) {
            throw new RuntimeException("Feriado no encontrado con id: " + id);
        }
        feriadoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean validarDuracion(int duracionHoras) {
        ConfiguracionGeneralEntity config = getConfiguracionGeneral().orElseThrow(
                () -> new IllegalStateException("Configuración general no encontrada.")
        );
        return duracionHoras >= config.getDuracionMinimaHorasReserva() &&
                duracionHoras <= config.getDuracionMaximaHorasReserva() &&
                (config.getIntervaloPermitidoHorasReserva() == 0 || duracionHoras % config.getIntervaloPermitidoHorasReserva() == 0);
    }
}
