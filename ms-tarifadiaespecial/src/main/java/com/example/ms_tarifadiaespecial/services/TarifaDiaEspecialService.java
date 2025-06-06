package com.example.ms_tarifadiaespecial.services;

import com.example.ms_tarifadiaespecial.entities.TarifaDiaEspecialEntity;
import com.example.ms_tarifadiaespecial.repositories.TarifaDiaEspecialRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TarifaDiaEspecialService {

    @Autowired
    private TarifaDiaEspecialRepository repository;

    public List<TarifaDiaEspecialEntity> getAll() {
        return repository.findAll();
    }

    public Optional<TarifaDiaEspecialEntity> getById(Long id) {
        return repository.findById(id);
    }

    public TarifaDiaEspecialEntity save(TarifaDiaEspecialEntity entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Double aplicarTarifaEspecial(LocalDate fecha, Double precioBase, int cantidadPersonas, int cumpleanieros) {
        // 1. Verificar si es feriado
        Optional<TarifaDiaEspecialEntity> feriado = repository.findByFecha(fecha);
        double precio = precioBase;

        if (feriado.isPresent()) {
            Double recargo = feriado.get().getRecargoPorcentaje();
            if (recargo != null && recargo > 0) {
                precio *= (1 + recargo / 100.0);
            }
        } else if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            // 2. Verificar si es fin de semana (recargo fijo, por ejemplo 15%)
            precio *= 1.15;
        }

        // 3. Descuento por cumpleaños (según la rúbrica)
        double descuentoCumple = 0.0;
        if (cumpleanieros > 0) {
            if (cantidadPersonas >= 3 && cantidadPersonas <= 5) {
                descuentoCumple = (precio / cantidadPersonas) * 0.5 * Math.min(1, cumpleanieros);
            } else if (cantidadPersonas >= 6 && cantidadPersonas <= 10) {
                descuentoCumple = (precio / cantidadPersonas) * 0.5 * Math.min(2, cumpleanieros);
            }
        }

        return precio - descuentoCumple;
    }

    @PostConstruct
    public void inicializarFestividades() {
        if (repository.count() == 0) {
            // Feriados nacionales y festividades comunes en Chile
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 1, 1), "Año Nuevo", 30.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 3, 28), "Viernes Santo", 25.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 5, 1), "Día del Trabajador", 20.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 5, 21), "Día de las Glorias Navales", 20.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 6, 29), "San Pedro y San Pablo", 15.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 7, 16), "Virgen del Carmen", 15.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 8, 15), "Asunción de la Virgen", 15.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 9, 18), "Fiestas Patrias", 40.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 9, 19), "Día de las Glorias del Ejército", 40.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 10, 12), "Encuentro de Dos Mundos", 15.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 10, 31), "Día de las Iglesias Evangélicas", 10.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 11, 1), "Día de Todos los Santos", 15.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 12, 8), "Inmaculada Concepción", 20.0, true));
            repository.save(new TarifaDiaEspecialEntity(null, LocalDate.of(2025, 12, 25), "Navidad", 35.0, true));
            // Ejemplo de fin de semana genérico (no se usa fecha, solo para referencia)
            repository.save(new TarifaDiaEspecialEntity(null, null, "Fin de semana", 15.0, false));
        }
    }
}