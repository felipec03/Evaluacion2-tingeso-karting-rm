package com.example.ms_tarifadiaespecial.repositories;

import com.example.ms_tarifadiaespecial.entities.TarifaDiaEspecialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TarifaDiaEspecialRepository extends JpaRepository<TarifaDiaEspecialEntity, Long> {
    Optional<TarifaDiaEspecialEntity> findByFecha(LocalDate fecha);
}