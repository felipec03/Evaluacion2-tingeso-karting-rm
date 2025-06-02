package com.example.ms_descuentoporpersona.repositories;

import com.example.ms_descuentoporpersona.entities.DescuentoPorPersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescuentoPorPersonaRepository extends JpaRepository<DescuentoPorPersonaEntity, Long> {
    List<DescuentoPorPersonaEntity> findByActivoTrue();
    @Query("SELECT d FROM DescuentoPorPersonaEntity d WHERE d.activo = true AND d.minPersonas <= :numeroPersonas AND d.maxPersonas >= :numeroPersonas ORDER BY d.porcentajeDescuento DESC")
    List<DescuentoPorPersonaEntity> findMejorDescuentoAplicable(@Param("numeroPersonas") int numeroPersonas);
}