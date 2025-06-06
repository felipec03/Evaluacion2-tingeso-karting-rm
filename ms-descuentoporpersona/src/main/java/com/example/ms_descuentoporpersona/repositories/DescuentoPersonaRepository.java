package com.example.ms_descuentoporpersona.repositories;

import com.example.ms_descuentoporpersona.entities.DescuentoPersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescuentoPersonaRepository extends JpaRepository<DescuentoPersonaEntity, Long> {

    List<DescuentoPersonaEntity> findByActivoTrue();

    @Query("SELECT d FROM DescuentoPersonaEntity d WHERE d.personasMin <= :personas AND d.personasMax >= :personas AND d.activo = true ORDER BY d.porcentajeDescuento DESC")
    List<DescuentoPersonaEntity> findDescuentoByPersonas(@Param("personas") int personas);
}