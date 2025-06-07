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
     * Encuentra reservas que se solapan con el periodo dado usando una consulta nativa de PostgreSQL.
     * Una reserva existente (r) se solapa con una nueva (n) si:
     * (r.fecha_hora < n.finNuevaReserva) Y (r.fecha_hora + (r.duracion_horas * INTERVAL '1 hour')) > n.inicioNuevaReserva)
     * Nota: Los nombres de columna en la consulta nativa deben coincidir con los nombres reales en la base de datos.
     */
    @Query(value = "SELECT r.* FROM reservas r WHERE r.estado_reserva NOT IN ('CANCELADA', 'PAGADA_Y_FINALIZADA') AND " +
            "r.fecha_hora < :finNuevaReserva AND " +
            "(r.fecha_hora + (r.duracion_horas * INTERVAL '1 hour')) > :inicioNuevaReserva",
            nativeQuery = true)
    List<ReservaEntity> findReservasSolapadas(@Param("inicioNuevaReserva") LocalDateTime inicioNuevaReserva,
                                              @Param("finNuevaReserva") LocalDateTime finNuevaReserva);

    List<ReservaEntity> findByRutUsuario(String rutUsuario);

}
