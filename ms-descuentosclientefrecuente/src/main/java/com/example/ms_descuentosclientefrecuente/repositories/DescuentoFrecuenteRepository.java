package com.example.ms_descuentosclientefrecuente.repositories;

import com.example.ms_descuentosclientefrecuente.entities.DescuentoFrecuenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DescuentoFrecuenteRepository extends JpaRepository<DescuentoFrecuenteEntity, Long> {
    Optional<DescuentoFrecuenteEntity> findByRutClienteAndMesAndAnio(String rutCliente, Integer mes, Integer anio);
}