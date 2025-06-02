package com.example.ms_registroreserva_comprobantepago.repositories;

import com.example.ms_registroreserva_comprobantepago.entities.ComprobantePagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ComprobantePagoRepository extends JpaRepository<ComprobantePagoEntity, Long> {
    Optional<ComprobantePagoEntity> findByReservaId(Long reservaId);
    List<ComprobantePagoEntity> findByEmailCliente(String emailCliente);
    Optional<ComprobantePagoEntity> findByCodigoComprobante(String codigoComprobante);
}