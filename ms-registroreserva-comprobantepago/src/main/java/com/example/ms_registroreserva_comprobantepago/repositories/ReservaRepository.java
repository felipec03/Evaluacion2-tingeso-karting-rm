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

    // Para verificar disponibilidad: karts ocupados en un intervalo de tiempo
    // Se asume que cada reserva tiene una fechaHora de inicio y una duración
    // Esta query busca reservas que se solapan con el intervalo [inicioNuevaReserva, finNuevaReserva)
    @Query("SELECT r FROM ReservaEntity r WHERE r.estadoReserva NOT IN ('CANCELADA') AND " +
            "((r.fechaHora < :finNuevaReserva AND FUNCTION('ADDTIME', r.fechaHora, FUNCTION('MAKETIME', r.duracionHoras, 0, 0)) > :inicioNuevaReserva))")
    List<ReservaEntity> findReservasSolapadas(@Param("inicioNuevaReserva") LocalDateTime inicioNuevaReserva,
                                              @Param("finNuevaReserva") LocalDateTime finNuevaReserva);

}
