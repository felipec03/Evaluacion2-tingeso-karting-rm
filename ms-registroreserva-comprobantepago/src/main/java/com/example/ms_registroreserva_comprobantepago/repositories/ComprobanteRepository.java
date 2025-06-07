package com.example.ms_registroreserva_comprobantepago.repositories;

import com.example.ms_registroreserva_comprobantepago.entities.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long> {
    Optional<ComprobanteEntity> findByIdReserva(Long idReserva);
    Optional<ComprobanteEntity> findByCodigoComprobante(String codigoComprobante);
}
