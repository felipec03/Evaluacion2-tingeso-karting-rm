package com.example.ms_registroreserva_comprobantepago.services;

import com.example.ms_registroreserva_comprobantepago.dtos.TarifaDTO;
import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import com.example.ms_registroreserva_comprobantepago.feignclients.DescuentoFrecuenteFeignClient;
import com.example.ms_registroreserva_comprobantepago.feignclients.DescuentoPersonaFeignClient;
import com.example.ms_registroreserva_comprobantepago.feignclients.TarifaConfigFeignClient;
import com.example.ms_registroreserva_comprobantepago.feignclients.TarifaDiaEspecialFeignClient;
import com.example.ms_registroreserva_comprobantepago.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ReservaService {

    private static final Logger LOGGER = Logger.getLogger(ReservaService.class.getName());

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private DescuentoPersonaFeignClient descuentoPersonaFeignClient;

    @Autowired
    private TarifaConfigFeignClient tarifaConfigFeignClient;

    @Autowired
    private DescuentoFrecuenteFeignClient descuentoFrecuenteFeignClient;

    @Autowired
    private TarifaDiaEspecialFeignClient tarifaDiaEspecialFeignClient;

    @Value("${app.karting.max-karts:20}")
    private int maxKartsDisponibles;

    public List<ReservaEntity> getAllReservas() {
        return reservaRepository.findAll();
    }

    public Optional<ReservaEntity> getReservaById(Long id) {
        return reservaRepository.findById(id);
    }

    private int getDuracionPorTipoReserva(int tipoReserva) {
        return switch (tipoReserva) {
            case 1 -> 30; // Tipo 1 (10 vueltas): 30 minutos
            case 2 -> 35; // Tipo 2 (15 vueltas): 35 minutos
            case 3 -> 40; // Tipo 3 (20 vueltas): 40 minutos
            default -> 30; // Duración por defecto si el tipo no está definido
        };
    }

    public boolean checkDisponibilidadInterna(LocalDateTime inicioNuevaReserva, int duracionMinutosNuevaReserva, int cantidadPersonasNuevaReserva) {
        LocalDateTime finNuevaReserva = inicioNuevaReserva.plusMinutes(duracionMinutosNuevaReserva);
        List<ReservaEntity> reservasSolapadas = reservaRepository.findReservasSolapadas(inicioNuevaReserva, finNuevaReserva);
        int kartsOcupados = 0;
        for (ReservaEntity existente : reservasSolapadas) {
            kartsOcupados += existente.getCantidadPersonas();
        }
        return (kartsOcupados + cantidadPersonasNuevaReserva) <= maxKartsDisponibles;
    }

    @Transactional
    public ReservaEntity crearReserva(ReservaEntity reserva) {
        reserva.setId(null); // Ensure creation

        // Determine and set duracionMinutos if not provided or invalid
        if (reserva.getDuracionMinutos() <= 0) {
            reserva.setDuracionMinutos(getDuracionPorTipoReserva(reserva.getTipoReserva()));
        }

        if (!checkDisponibilidadInterna(reserva.getFechaHora(), reserva.getDuracionMinutos(), reserva.getCantidadPersonas())) {
            throw new RuntimeException("No hay disponibilidad de karts para la fecha, hora y cantidad de personas solicitadas.");
        }

        // 1. Get Base Price per person from M1 (ms-tarifasconfig)
        double precioBasePorPersonaM1;
        try {
            ResponseEntity<TarifaDTO> tarifaResponse = tarifaConfigFeignClient.getTarifaByTipoReserva(reserva.getTipoReserva());
            if (tarifaResponse.getStatusCode().is2xxSuccessful() && tarifaResponse.getBody() != null && tarifaResponse.getBody().getPrecioBasePorPersona() != null) {
                precioBasePorPersonaM1 = tarifaResponse.getBody().getPrecioBasePorPersona();
            } else {
                LOGGER.log(Level.SEVERE, "M1 Error: No se pudo obtener la tarifa base. Status: " + tarifaResponse.getStatusCode());
                throw new RuntimeException("No se pudo obtener la tarifa base de M1 (ms-tarifasconfig).");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "M1 Exception: Error al obtener tarifa: " + e.getMessage(), e);
            throw new RuntimeException("Error al comunicarse con el servicio de tarifas (M1): " + e.getMessage());
        }
        double montoBaseTotalM1 = precioBasePorPersonaM1 * reserva.getCantidadPersonas();
        reserva.setMontoBase(montoBaseTotalM1);

        // 2. Apply M4 (ms-tarifadiaespecial) adjustments
        LocalDate fechaReserva = reserva.getFechaHora().toLocalDate();
        double precioPostM4Recargos = montoBaseTotalM1; // Default if M4 call fails

        try {
            // Get price after M4's general recargos (sending 0 cumpleanieros)
            ResponseEntity<Double> responseRecargoM4 = tarifaDiaEspecialFeignClient.aplicarTarifa(
                    fechaReserva, montoBaseTotalM1, reserva.getCantidadPersonas(), 0);
            if (responseRecargoM4.getStatusCode().is2xxSuccessful() && responseRecargoM4.getBody() != null) {
                precioPostM4Recargos = responseRecargoM4.getBody();
            } else {
                LOGGER.log(Level.WARNING, "M4 Warning: No se pudo obtener el precio con recargo (sin desc. cumple). Usando montoBaseTotalM1. Status: " + responseRecargoM4.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "M4 Exception: Error al aplicar tarifa especial (recargos): " + e.getMessage(), e);
        }

        // Calculate M4's specific birthday discount amount, if applicable
        double montoDescuentoCumpleanosM4 = 0.0;
        int cantidadCumpleaneros = reserva.getCantidadCumple(); // Direct use as it's int

        if (cantidadCumpleaneros > 0) {
            try {
                ResponseEntity<Double> responseNetoConDescCumpleM4 = tarifaDiaEspecialFeignClient.aplicarTarifa(
                        fechaReserva, montoBaseTotalM1, reserva.getCantidadPersonas(), cantidadCumpleaneros);
                if (responseNetoConDescCumpleM4.getStatusCode().is2xxSuccessful() && responseNetoConDescCumpleM4.getBody() != null) {
                    double precioNetoDespuesM4Completo = responseNetoConDescCumpleM4.getBody();
                    montoDescuentoCumpleanosM4 = precioPostM4Recargos - precioNetoDespuesM4Completo;
                    if (montoDescuentoCumpleanosM4 < 0) {
                        LOGGER.log(Level.WARNING, "M4 Warning: Descuento de cumpleaños resultó negativo. Ajustando a 0.");
                        montoDescuentoCumpleanosM4 = 0;
                    }
                } else {
                    LOGGER.log(Level.WARNING, "M4 Warning: No se pudo obtener el precio neto con descuento de cumpleaños. Status: " + responseNetoConDescCumpleM4.getStatusCode());
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "M4 Exception: Error al aplicar tarifa especial (desc. cumple): " + e.getMessage(), e);
            }
        }

        // 3. Calculate Discount from M2 (ms-descuentoporpersona)
        double montoDescuentoGrupoM2 = 0.0;
        double porcentajeDescuentoGrupo = 0.0;
        try {
            ResponseEntity<Double> descPersonaResponse = descuentoPersonaFeignClient.calcularDescuentoPorPersonas(reserva.getCantidadPersonas());
            if (descPersonaResponse.getStatusCode().is2xxSuccessful() && descPersonaResponse.getBody() != null) {
                porcentajeDescuentoGrupo = descPersonaResponse.getBody(); // This is a percentage
                montoDescuentoGrupoM2 = precioPostM4Recargos * (porcentajeDescuentoGrupo / 100.0);
            } else {
                LOGGER.log(Level.WARNING, "M2 Warning: No se pudo obtener descuento por personas. Status: " + descPersonaResponse.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "M2 Exception: Error al obtener descuento por personas: " + e.getMessage(), e);
        }

        // 4. Calculate Discount from M3 (ms-descuentosclientefrecuente)
        double montoDescuentoFrecuenteM3 = 0.0;
        double porcentajeDescuentoFrecuente = 0.0;
        if (reserva.getRutUsuario() != null && !reserva.getRutUsuario().isEmpty()) {
            try {
                ResponseEntity<Double> descFrecuenteResponse = descuentoFrecuenteFeignClient.obtenerDescuentoActual(reserva.getRutUsuario());
                if (descFrecuenteResponse.getStatusCode().is2xxSuccessful() && descFrecuenteResponse.getBody() != null) {
                    porcentajeDescuentoFrecuente = descFrecuenteResponse.getBody(); // This is a percentage
                    montoDescuentoFrecuenteM3 = precioPostM4Recargos * (porcentajeDescuentoFrecuente / 100.0);
                } else {
                    LOGGER.log(Level.WARNING, "M3 Warning: No se pudo obtener descuento cliente frecuente. Status: " + descFrecuenteResponse.getStatusCode());
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "M3 Exception: Error al obtener descuento cliente frecuente: " + e.getMessage(), e);
            }
        }

        // 5. Determine Final Discount (most beneficial of M2, M3, and M4's birthday discount)
        double montoDescuentoAplicado = 0.0;
        double porcentajeDescuentoAplicadoFinal = 0.0;

        if (montoDescuentoGrupoM2 > montoDescuentoAplicado) {
            montoDescuentoAplicado = montoDescuentoGrupoM2;
            porcentajeDescuentoAplicadoFinal = porcentajeDescuentoGrupo;
        }
        if (montoDescuentoFrecuenteM3 > montoDescuentoAplicado) {
            montoDescuentoAplicado = montoDescuentoFrecuenteM3;
            porcentajeDescuentoAplicadoFinal = porcentajeDescuentoFrecuente;
        }
        if (montoDescuentoCumpleanosM4 > montoDescuentoAplicado) {
            montoDescuentoAplicado = montoDescuentoCumpleanosM4;
            if (precioPostM4Recargos > 0) { // Calculate effective percentage for birthday discount
                porcentajeDescuentoAplicadoFinal = (montoDescuentoCumpleanosM4 / precioPostM4Recargos) * 100.0;
            } else {
                porcentajeDescuentoAplicadoFinal = (montoDescuentoCumpleanosM4 > 0) ? 100.0 : 0.0; // If base is 0, any discount is effectively 100% of that 0 base
            }
        }

        reserva.setPorcentajeDescuentoAplicado(porcentajeDescuentoAplicadoFinal);
        reserva.setMontoDescuento(montoDescuentoAplicado);

        // 6. Calculate Final Amount (Subtotal before IVA)
        double montoSubtotal = precioPostM4Recargos - montoDescuentoAplicado;
        reserva.setMontoFinal(montoSubtotal);

        // 7. Set initial state
        reserva.setEstadoReserva("PENDIENTE");

        return reservaRepository.save(reserva);
    }

    @Transactional
    public ReservaEntity actualizarEstadoReserva(Long idReserva, String nuevoEstado) {
        ReservaEntity reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));
        reserva.setEstadoReserva(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void deleteReserva(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada para eliminar con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }
}
