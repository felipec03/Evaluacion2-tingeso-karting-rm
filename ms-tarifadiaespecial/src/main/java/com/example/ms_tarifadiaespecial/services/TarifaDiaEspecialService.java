package com.example.ms_tarifadiaespecial.services;

import com.example.ms_tarifadiaespecial.entities.TarifaDiaEspecialEntity;
import com.example.ms_tarifadiaespecial.repositories.TarifaDiaEspecialRepository;
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
}