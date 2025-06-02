package com.example.ms_registroreserva_comprobantepago.repositories;

import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
    List<ReservaEntity> findByClienteId(String clienteId);
    List<ReservaEntity> findByEmailCliente(String emailCliente);
    // Para simular checkDisponibilidad (simplificado, idealmente esto es un MS aparte)
    List<ReservaEntity> findByFechaReservaAndInicioReservaBetween(LocalDate fechaReserva, LocalDateTime inicio, LocalDateTime fin);
    List<ReservaEntity> findByFechaReservaAndFinReservaBetween(LocalDate fechaReserva, LocalDateTime inicio, LocalDateTime fin);
    List<ReservaEntity> findByFechaReservaAndInicioReservaLessThanEqualAndFinReservaGreaterThanEqual(LocalDate fechaReserva, LocalDateTime fin, LocalDateTime inicio);

}