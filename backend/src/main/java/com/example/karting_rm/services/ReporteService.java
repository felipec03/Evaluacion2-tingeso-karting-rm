package com.example.karting_rm.services;

import com.example.karting_rm.dtos.ReporteFilaDTO;
import com.example.karting_rm.entities.ComprobanteEntity;
import com.example.karting_rm.entities.ReservaEntity;
import com.example.karting_rm.repositories.ComprobanteRepository;
import com.example.karting_rm.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    // Define these based on your system's actual values and int mapping for tiporeserva
    // Example: 1 -> "10 VUELTAS", 2 -> "15 VUELTAS", etc.
    // This mapping should be robust and cover all tiporeserva values.
    private static final Map<Integer, String> TIPO_RESERVA_MAP = new HashMap<>() {{
        put(1, "10 VUELTAS"); // Assuming 1 is for 10 vueltas
        put(2, "15 VUELTAS"); // Assuming 2 is for 15 vueltas
        put(3, "20 VUELTAS"); // Assuming 3 is for 20 vueltas
        put(4, "TIEMPO_MAX_30MIN"); // Assuming 4 is for tiempo max 30 min
        put(5, "TIEMPO_MAX_60MIN"); // Assuming 5 is for tiempo max 60 min
        // Add other mappings as per your system's 'tiporeserva' integer codes
    }};

    private static final List<String> TIPOS_RESERVA_CATEGORIAS = new ArrayList<>(TIPO_RESERVA_MAP.values());


    private static final List<String> RANGOS_PERSONAS_CATEGORIAS = Arrays.asList(
            "1-3 PERSONAS", "4-6 PERSONAS", "7-9 PERSONAS"
            // Add more ranges if needed
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

        for (ReservaEntity reserva : reservasEnRango) {
            Optional<ComprobanteEntity> optComprobante = comprobanteRepository.findByReservaId(reserva.getId());
            if (optComprobante.isPresent()) {
                ComprobanteEntity comprobante = optComprobante.get();
                String tipoReservaDesc = TIPO_RESERVA_MAP.get(reserva.getTiporeserva());
                String mesReserva = reserva.getFecha().format(MONTH_FORMATTER);

                if (tipoReservaDesc != null && reporteMap.containsKey(tipoReservaDesc) && mesesEnRango.contains(mesReserva)) {
                    reporteMap.get(tipoReservaDesc).agregarIngreso(mesReserva, (double) comprobante.getTotal());
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

        for (ReservaEntity reserva : reservasEnRango) {
            Optional<ComprobanteEntity> optComprobante = comprobanteRepository.findByReservaId(reserva.getId());
            if (optComprobante.isPresent()) {
                ComprobanteEntity comprobante = optComprobante.get();
                String mesReserva = reserva.getFecha().format(MONTH_FORMATTER);
                String rangoPersonas = determinarRangoPersonas(reserva.getNumero_personas());

                if (rangoPersonas != null && reporteMap.containsKey(rangoPersonas) && mesesEnRango.contains(mesReserva)) {
                    reporteMap.get(rangoPersonas).agregarIngreso(mesReserva, (double) comprobante.getTotal());
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
        // Consider adding a default or "Other" category if needed
        return null;
    }
}