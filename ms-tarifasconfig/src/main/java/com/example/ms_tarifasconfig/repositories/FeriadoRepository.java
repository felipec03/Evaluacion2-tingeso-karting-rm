package com.example.ms_tarifasconfig.repositories;

import com.example.ms_tarifasconfig.entities.FeriadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FeriadoRepository extends JpaRepository<FeriadoEntity, Long> {
    Optional<FeriadoEntity> findByFecha(String fecha); // fecha as "MM-dd"
}