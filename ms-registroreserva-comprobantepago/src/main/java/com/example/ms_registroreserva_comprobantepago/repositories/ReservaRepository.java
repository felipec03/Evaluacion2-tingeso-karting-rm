package com.example.ms_registroreserva_comprobantepago.repositories;


import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
    /**
     * Encuentra reservas que se solapan con el periodo dado.
     * Una reserva existente (r) se solapa con una nueva (n) si:
     * (r.fechaHora < n.finNuevaReserva) Y (r.fechaHora + r.duracionHoras > n.inicioNuevaReserva)
     * Se usa FUNCTION('make_interval', ...) para sumar duracionHoras a fechaHora, asumiendo PostgreSQL.
     * Los argumentos para make_interval son: years, months, weeks, days, hours, mins, secs.
     */
    @Query("SELECT r FROM ReservaEntity r WHERE r.estadoReserva NOT IN ('CANCELADA', 'PAGADA_Y_FINALIZADA') AND " +
            "(r.fechaHora < :finNuevaReserva AND " +
            "(r.fechaHora + FUNCTION('make_interval', 0, 0, 0, 0, r.duracionHoras, 0, 0.0)) > :inicioNuevaReserva)")
    List<ReservaEntity> findReservasSolapadas(@Param("inicioNuevaReserva") LocalDateTime inicioNuevaReserva,
                                              @Param("finNuevaReserva") LocalDateTime finNuevaReserva);

    List<ReservaEntity> findByRutUsuario(String rutUsuario);

}
