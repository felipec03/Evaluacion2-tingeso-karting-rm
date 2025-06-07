package com.example.ms_racksemanal.repositories;

import com.example.ms_racksemanal.entities.RackSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackSemanalRepository extends JpaRepository<RackSemanal, Long> {
    List<RackSemanal> findByDiaSemana(String diaSemana);
    List<RackSemanal> findByBloqueTiempo(String bloqueTiempo);
    RackSemanal findByDiaSemanaAndBloqueTiempo(String diaSemana, String bloqueTiempo);
    List<RackSemanal> findByReservado(boolean reservado);
    RackSemanal findByReservaId(Long reservaId);
}
