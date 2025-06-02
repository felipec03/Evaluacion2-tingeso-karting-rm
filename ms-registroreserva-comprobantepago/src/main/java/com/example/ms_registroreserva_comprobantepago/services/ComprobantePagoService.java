package com.example.ms_registroreserva_comprobantepago.services;

import com.example.ms_registroreserva_comprobantepago.dtos.ComprobantePagoDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.CrearPagoDTO;
import com.example.ms_registroreserva_comprobantepago.entities.ComprobantePagoEntity;
import com.example.ms_registroreserva_comprobantepago.entities.EstadoPago;
import com.example.ms_registroreserva_comprobantepago.entities.EstadoReserva;
import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import com.example.ms_registroreserva_comprobantepago.repositories.ComprobantePagoRepository;
import com.example.ms_registroreserva_comprobantepago.repositories.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComprobantePagoService {

    @Autowired
    private ComprobantePagoRepository comprobantePagoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public ComprobantePagoDTO registrarPago(CrearPagoDTO crearPagoDTO) {
        ReservaEntity reserva = reservaRepository.findById(crearPagoDTO.getIdReserva())
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + crearPagoDTO.getIdReserva()));

        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("No se puede registrar un pago para una reserva cancelada o completada.");
        }

        // Lógica para verificar si el pago excede el monto total, etc.
        BigDecimal totalPagadoHastaAhora = reserva.getPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.PAGADO)
                .map(ComprobantePagoEntity::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPagadoHastaAhora.add(crearPagoDTO.getMontoPagado()).compareTo(reserva.getPrecioTotal()) > 0) {
            // Podría permitirse sobrepago o ajustarse, aquí lo lanzamos como error
            // throw new IllegalArgumentException("El monto del pago excede el saldo pendiente de la reserva.");
        }


        ComprobantePagoEntity pagoEntity = modelMapper.map(crearPagoDTO, ComprobantePagoEntity.class);
        pagoEntity.setReserva(reserva);
        pagoEntity.setEstadoPago(EstadoPago.PAGADO); // Asumimos que el registro implica un pago exitoso

        ComprobantePagoEntity savedPago = comprobantePagoRepository.save(pagoEntity);

        // Actualizar estado de la reserva si es necesario
        BigDecimal nuevoTotalPagado = totalPagadoHastaAhora.add(savedPago.getMontoPagado());
        if (nuevoTotalPagado.compareTo(reserva.getPrecioTotal()) >= 0) {
            reserva.setEstado(EstadoReserva.CONFIRMADA); // O PAGADA_TOTALMENTE si tienes ese estado
        } else if (nuevoTotalPagado.compareTo(BigDecimal.ZERO) > 0) {
            reserva.setEstado(EstadoReserva.PAGADA_PARCIALMENTE);
        }
        reservaRepository.save(reserva); // Guardar la reserva actualizada

        ComprobantePagoDTO dto = modelMapper.map(savedPago, ComprobantePagoDTO.class);
        dto.setIdReserva(reserva.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public ComprobantePagoDTO obtenerComprobantePorId(Long id) {
        ComprobantePagoEntity pagoEntity = comprobantePagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante de pago no encontrado con ID: " + id));
        ComprobantePagoDTO dto = modelMapper.map(pagoEntity, ComprobantePagoDTO.class);
        if (pagoEntity.getReserva() != null) {
            dto.setIdReserva(pagoEntity.getReserva().getId());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ComprobantePagoDTO> obtenerComprobantesPorReserva(Long idReserva) {
        if (!reservaRepository.existsById(idReserva)) {
            throw new EntityNotFoundException("Reserva no encontrada con ID: " + idReserva);
        }
        List<ComprobantePagoEntity> pagos = comprobantePagoRepository.findByReservaId(idReserva);
        return pagos.stream()
                .map(pago -> {
                    ComprobantePagoDTO dto = modelMapper.map(pago, ComprobantePagoDTO.class);
                    dto.setIdReserva(idReserva);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // El "Comprobante de Pago para Clientes" podría ser simplemente la ReservaConPagosDTO
    // o un formato más específico si se requiere. Usaremos ReservaService.obtenerReservaConPagos.
}