package com.example.ms_tarifasconfig.repositories;

import com.example.ms_tarifasconfig.entities.TarifaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TarifaRepository extends JpaRepository<TarifaEntity, Long> {
    Optional<TarifaEntity> findByTipoReservaAndActivaTrue(Integer tipoReserva);
    List<TarifaEntity> findAllByActivaTrue();
}