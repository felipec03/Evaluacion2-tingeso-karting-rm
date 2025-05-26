package com.example.karting_rm.services;

import com.example.karting_rm.dtos.ReporteFilaDTO;
import com.example.karting_rm.entities.ComprobanteEntity;
import com.example.karting_rm.entities.ReservaEntity;
import com.example.karting_rm.repositories.ComprobanteRepository;
import com.example.karting_rm.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReporteService {

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    // Aligning with ReservaService and data.sql tiporeserva values
    // Assuming: 1 -> Adulto, 2 -> Niño, 3 -> Mixta (as per ReservaService logic)
    // You might need to adjust these category names based on actual business definitions.
    private static final Map<Integer, String> TIPO_RESERVA_MAP = new HashMap<>() {{
        put(1, "ADULTO");
        put(2, "NIÑO");
        put(3, "MIXTA");
        // If there are other tipoReserva values used, add them here.
        // The original "10 VUELTAS", "15 VUELTAS" etc. did not seem to align
        // with the integer tiporeserva values in ReservaEntity/data.sql
    }};

    private static final List<String> TIPOS_RESERVA_CATEGORIAS = new ArrayList<>(TIPO_RESERVA_MAP.values());


    private static final List<String> RANGOS_PERSONAS_CATEGORIAS = Arrays.asList(
            "1-3 PERSONAS", "4-6 PERSONAS", "7-9 PERSONAS"
            // Add more ranges if needed, e.g., "10+ PERSONAS"
    );
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public List<ReporteFilaDTO> generarReporteIngresosPorTipoReserva(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReservaEntity> reservasEnRango = reservaRepository.findByFechaBetween(fechaInicio, fechaFin);
        Map<String, ReporteFilaDTO> reporteMap = new LinkedHashMap<>();
        List<String> mesesEnRango = getMesesEnRango(fechaInicio, fechaFin);

        // Initialize DTOs for all categories and months
        for (String tipoCategoria : TIPOS_RESERVA_CATEGORIAS) {
            ReporteFilaDTO fila = new ReporteFilaDTO(tipoCategoria);
            for (String mes : mesesEnRango) {
                fila.inicializarMes(mes);
            }
            reporteMap.put(tipoCategoria, fila);
        }
        // Add a default category for reservations whose tiporeserva is not in TIPO_RESERVA_MAP
        String categoriaDesconocida = "OTRO TIPO RESERVA";
        if (!reporteMap.containsKey(categoriaDesconocida)) {
            ReporteFilaDTO filaDesconocida = new ReporteFilaDTO(categoriaDesconocida);
            for (String mes : mesesEnRango) {
                filaDesconocida.inicializarMes(mes);
            }
            reporteMap.put(categoriaDesconocida, filaDesconocida);
        }


        for (ReservaEntity reserva : reservasEnRango) {
            Optional<ComprobanteEntity> optComprobante = comprobanteRepository.findByReservaId(reserva.getId());
            if (optComprobante.isPresent()) {
                String categoriaReserva = TIPO_RESERVA_MAP.get(reserva.getTiporeserva());
                if (categoriaReserva == null) {
                    categoriaReserva = categoriaDesconocida; // Assign to default if not mapped
                }

                ReporteFilaDTO fila = reporteMap.get(categoriaReserva);
                // It's possible fila is null if a new tiporeserva appears that wasn't initialized
                // For robustness, ensure fila is not null or handle this case
                if (fila != null) {
                    String mesReserva = reserva.getFecha().format(MONTH_FORMATTER);
                    // Ensure totalConIva is not null; provide a default if necessary
                    BigDecimal monto = BigDecimal.valueOf(reserva.getTotalConIva());
                    fila.agregarIngreso(mesReserva, monto);
                }
            }
        }
        return new ArrayList<>(reporteMap.values());
    }

    public List<ReporteFilaDTO> generarReporteIngresosPorNumeroPersonas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReservaEntity> reservasEnRango = reservaRepository.findByFechaBetween(fechaInicio, fechaFin);
        Map<String, ReporteFilaDTO> reporteMap = new LinkedHashMap<>();
        List<String> mesesEnRango = getMesesEnRango(fechaInicio, fechaFin);

        // Initialize DTOs
        for (String rango : RANGOS_PERSONAS_CATEGORIAS) {
            ReporteFilaDTO fila = new ReporteFilaDTO(rango);
            for (String mes : mesesEnRango) {
                fila.inicializarMes(mes);
            }
            reporteMap.put(rango, fila);
        }
        // Add a default category for reservations whose numero_personas doesn't fit defined ranges
        String categoriaOtroRango = "OTRO RANGO PERSONAS";
        if (!reporteMap.containsKey(categoriaOtroRango)) {
            ReporteFilaDTO filaOtroRango = new ReporteFilaDTO(categoriaOtroRango);
            for (String mes : mesesEnRango) {
                filaOtroRango.inicializarMes(mes);
            }
            reporteMap.put(categoriaOtroRango, filaOtroRango);
        }

        for (ReservaEntity reserva : reservasEnRango) {
            Optional<ComprobanteEntity> optComprobante = comprobanteRepository.findByReservaId(reserva.getId());
            if (optComprobante.isPresent()) {
                String categoriaRangoPersonas = determinarRangoPersonas(reserva.getNumeroPersonas());
                if (categoriaRangoPersonas == null) {
                    categoriaRangoPersonas = categoriaOtroRango; // Assign to default if not mapped
                }

                ReporteFilaDTO fila = reporteMap.get(categoriaRangoPersonas);
                // Ensure fila is not null
                if (fila != null) {
                    String mesReserva = reserva.getFecha().format(MONTH_FORMATTER);
                    BigDecimal monto = BigDecimal.valueOf(reserva.getTotalConIva());
                    fila.agregarIngreso(mesReserva, monto);
                }
            }
        }
        return new ArrayList<>(reporteMap.values());
    }

    private List<String> getMesesEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<String> meses = new ArrayList<>();
        YearMonth currentMonth = YearMonth.from(fechaInicio.withDayOfMonth(1));
        YearMonth endMonth = YearMonth.from(fechaFin.withDayOfMonth(1));

        while (!currentMonth.isAfter(endMonth)) {
            meses.add(currentMonth.format(MONTH_FORMATTER));
            currentMonth = currentMonth.plusMonths(1);
        }
        return meses;
    }

    private String determinarRangoPersonas(int numeroPersonas) {
        if (numeroPersonas >= 1 && numeroPersonas <= 3) {
            return "1-3 PERSONAS";
        } else if (numeroPersonas >= 4 && numeroPersonas <= 6) {
            return "4-6 PERSONAS";
        } else if (numeroPersonas >= 7 && numeroPersonas <= 9) {
            return "7-9 PERSONAS";
        }
        // Consider adding "10+ PERSONAS" or returning a specific "Other" category
        // if you want to capture reservations outside these specific ranges.
        // Returning null means they won't be categorized unless handled by the caller.
        return null;
    }
}