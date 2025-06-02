package com.example.ms_registroreserva_comprobantepago.services;

import com.example.ms_registroreserva_comprobantepago.dtos.ComprobantePagoDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.CrearReservaDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.ReservaConPagosDTO;
import com.example.ms_registroreserva_comprobantepago.dtos.ReservaDTO;
import com.example.ms_registroreserva_comprobantepago.entities.EstadoReserva;
import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import com.example.ms_registroreserva_comprobantepago.repositories.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public ReservaDTO registrarReserva(CrearReservaDTO crearReservaDTO) {
        // Lógica de validación adicional si es necesaria:
        // - Verificar disponibilidad de habitación (requeriría comunicación con ms-habitaciones)
        // - Validar que fechaSalida > fechaEntrada
        if (crearReservaDTO.getFechaSalida().isBefore(crearReservaDTO.getFechaEntrada()) ||
                crearReservaDTO.getFechaSalida().isEqual(crearReservaDTO.getFechaEntrada())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }

        ReservaEntity reservaEntity = modelMapper.map(crearReservaDTO, ReservaEntity.class);
        // El estado PENDIENTE y fechas de creación/actualización se manejan con @PrePersist

        // Aquí podría ir la lógica de cálculo de precio si no viene en el DTO
        // reservaEntity.setPrecioTotal(calcularPrecio(crearReservaDTO.getIdHabitacion(), ...));

        ReservaEntity savedReserva = reservaRepository.save(reservaEntity);
        return modelMapper.map(savedReserva, ReservaDTO.class);
    }

    @Transactional(readOnly = true)
    public ReservaDTO obtenerReservaPorId(Long id) {
        ReservaEntity reservaEntity = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + id));
        return modelMapper.map(reservaEntity, ReservaDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorCliente(Long idCliente) {
        List<ReservaEntity> reservas = reservaRepository.findByIdCliente(idCliente);
        return reservas.stream()
                .map(reserva -> modelMapper.map(reserva, ReservaDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaDTO actualizarEstadoReserva(Long id, EstadoReserva nuevoEstado) {
        ReservaEntity reservaEntity = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + id));

        // Lógica de validación de transición de estados si es necesaria
        // Ej: No se puede confirmar una reserva cancelada directamente.
        reservaEntity.setEstado(nuevoEstado);
        ReservaEntity updatedReserva = reservaRepository.save(reservaEntity);
        return modelMapper.map(updatedReserva, ReservaDTO.class);
    }

    @Transactional
    public ReservaDTO cancelarReserva(Long id) {
        return actualizarEstadoReserva(id, EstadoReserva.CANCELADA);
    }

    @Transactional(readOnly = true)
    public ReservaConPagosDTO obtenerReservaConPagos(Long idReserva) {
        ReservaEntity reservaEntity = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + idReserva));

        ReservaConPagosDTO dto = modelMapper.map(reservaEntity, ReservaConPagosDTO.class);

        if (reservaEntity.getPagos() != null) {
            List<ComprobantePagoDTO> pagosDTO = reservaEntity.getPagos().stream()
                    .map(pago -> {
                        ComprobantePagoDTO pagoDTO = modelMapper.map(pago, ComprobantePagoDTO.class);
                        pagoDTO.setIdReserva(reservaEntity.getId()); // Asegurar que el idReserva esté en el DTO del pago
                        return pagoDTO;
                    })
                    .collect(Collectors.toList());
            dto.setPagos(pagosDTO);
        }
        return dto;
    }
}