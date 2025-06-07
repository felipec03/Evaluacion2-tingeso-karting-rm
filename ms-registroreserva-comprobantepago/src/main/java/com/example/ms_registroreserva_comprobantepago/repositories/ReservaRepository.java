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
    @Query("SELECT r FROM ReservaEntity r WHERE r.estadoReserva NOT IN ('CANCELADA') AND " +
            "((r.fechaHora < :finNuevaReserva AND (r.fechaHora + FUNCTION('make_time', r.duracionHoras, 0, 0.0)) > :inicioNuevaReserva))")
    List<ReservaEntity> findReservasSolapadas(@Param("inicioNuevaReserva") LocalDateTime inicioNuevaReserva,
                                              @Param("finNuevaReserva") LocalDateTime finNuevaReserva);

    List<ReservaEntity> findByRutUsuario(String rutUsuario);

}
