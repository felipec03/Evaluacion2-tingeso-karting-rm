package com.example.ms_descuentosclientefrecuente.repositories;

import com.example.ms_descuentosclientefrecuente.entities.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    // Counts reservations for a specific email within a date range
    long countByEmailArrendatarioAndFechaBetween(String emailArrendatario, LocalDate startDate, LocalDate endDate);
}