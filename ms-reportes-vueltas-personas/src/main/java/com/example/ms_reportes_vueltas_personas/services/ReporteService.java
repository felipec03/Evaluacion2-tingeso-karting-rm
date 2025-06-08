package com.example.ms_reportes_vueltas_personas.services;

import com.example.ms_reportes_vueltas_personas.dto.ReporteFilaDTO;
import com.example.ms_reportes_vueltas_personas.dto.ReporteResponseDTO;
import com.example.ms_reportes_vueltas_personas.dto.ReservaDTO;
import com.example.ms_reportes_vueltas_personas.feignclient.RegistroReservaFeignClient;
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
    private RegistroReservaFeignClient reservaFeignClient;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final List<String> RANGOS_PERSONAS_DEFINIDOS = List.of("1-2 Personas", "3-5 Personas", "6-10 Personas", "11-15 Personas");

    // As per requirement: "10, 15 o 20 vueltas"
    // These are the "defined in the system" vuelta-based tariffs.
    private static final List<String> PREDEFINED_VUELTA_TARIFAS_DESCRIPTIONS = List.of("10 Vueltas", "15 Vueltas", "20 Vueltas");

    /**
     * Derives the tariff category string from a ReservaDTO.
     * This logic needs to align with how 'tipoReserva' and 'duracionMinutos'
     * define the tariff types in the system (e.g., ms-tarifasconfig or business rules).
     */
    private String getReportTariffCategory(ReservaDTO reserva) {
        if (reserva.getDuracionMinutos() != null && reserva.getDuracionMinutos() > 0) {
            return reserva.getDuracionMinutos() + " Minutos";
        }

        // This mapping is an EXAMPLE. It MUST be validated against your system's actual logic
        // for how 'tipoReserva' (integer) maps to the descriptive vuelta counts.
        // If 'tipoReserva' directly implies a specific vuelta count (e.g., from ms-tarifasconfig),
        // that logic should be replicated or fetched.
        return switch (reserva.getTipoReserva()) {
            case 1 -> // Assuming tipoReserva 1 maps to "10 Vueltas"
                    "10 Vueltas";
            case 2 -> // Assuming tipoReserva 2 maps to "15 Vueltas"
                    "15 Vueltas";
            case 3 -> // Assuming tipoReserva 3 maps to "20 Vueltas"
                    "20 Vueltas";
            // Add more cases if other tipoReserva values map to specific vuelta counts
            default ->
                // If it's not time-based and doesn't map to a predefined vuelta count,
                // it's an undefined or other category.
                    "Tarifa por Vueltas (Otro)"; // Or handle as "Desconocida"
        };
    }

    public ReporteResponseDTO generarReporteIngresosPorTarifa(int anioInicio, int mesInicio, int anioFin, int mesFin) {
        List<ReservaDTO> todasLasReservas = reservaFeignClient.obtenerTodasLasReservas();
        if (todasLasReservas == null) {
            todasLasReservas = new ArrayList<>();
        }

        LocalDate fechaInicioReporte = LocalDate.of(anioInicio, mesInicio, 1);
        LocalDate fechaFinReporte = LocalDate.of(anioFin, mesFin, 1).withDayOfMonth(LocalDate.of(anioFin, mesFin, 1).lengthOfMonth());

        List<ReservaDTO> reservasProcesables = todasLasReservas.stream()
                .filter(r -> "PAGADA".equalsIgnoreCase(r.getEstadoReserva()))
                .filter(r -> r.getFechaHora() != null && r.getMontoFinal() != null && r.getMontoFinal() > 0) // Ensure essential data is present
                .filter(r -> {
                    LocalDate fechaReserva = r.getFechaHora().toLocalDate();
                    return !fechaReserva.isBefore(fechaInicioReporte) && !fechaReserva.isAfter(fechaFinReporte);
                })
                .toList();

        List<String> mesesDelRango = obtenerMesesEnRango(fechaInicioReporte, fechaFinReporte);

        Map<String, Map<String, Double>> ingresosPorTarifaYMes = reservasProcesables.stream()
                .collect(Collectors.groupingBy(
                        this::getReportTariffCategory, // Use the derivation method
                        Collectors.groupingBy(
                                r -> YearMonth.from(r.getFechaHora().toLocalDate()).format(MONTH_FORMATTER),
                                Collectors.summingDouble(ReservaDTO::getMontoFinal)
                        )
                ));

        // To meet "Todos los tipos ... definidos en el sistema se deben mostrar":
        // Start with predefined vuelta-based tariffs.
        Set<String> categoriasTarifaParaReporte = new LinkedHashSet<>(PREDEFINED_VUELTA_TARIFAS_DESCRIPTIONS);

        // Add any dynamically found time-based tariffs (e.g., "35 Minutos", "40 Minutos")
        // and any other derived categories from the actual data.
        reservasProcesables.stream()
                .map(this::getReportTariffCategory)
                .filter(Objects::nonNull)
                .forEach(categoriasTarifaParaReporte::add);

        List<ReporteFilaDTO> filasReporte = new ArrayList<>();
        // Sort categories for consistent report output (optional, but good)
        List<String> sortedCategorias = categoriasTarifaParaReporte.stream().sorted().toList();

        for (String tipoTarifa : sortedCategorias) {
            Map<String, Double> ingresosMes = new LinkedHashMap<>();
            double totalCategoria = 0.0;
            for (String mes : mesesDelRango) {
                double ingresoMes = ingresosPorTarifaYMes.getOrDefault(tipoTarifa, Collections.emptyMap()).getOrDefault(mes, 0.0);
                ingresosMes.put(mes, ingresoMes);
                totalCategoria += ingresoMes;
            }
            filasReporte.add(new ReporteFilaDTO(tipoTarifa, ingresosMes, totalCategoria));
        }

        return construirReporteResponse(filasReporte, mesesDelRango);
    }

    public ReporteResponseDTO generarReporteIngresosPorNumeroPersonas(int anioInicio, int mesInicio, int anioFin, int mesFin) {
        List<ReservaDTO> todasLasReservas = reservaFeignClient.obtenerTodasLasReservas();
        if (todasLasReservas == null) {
            todasLasReservas = new ArrayList<>();
        }

        LocalDate fechaInicioReporte = LocalDate.of(anioInicio, mesInicio, 1);
        LocalDate fechaFinReporte = LocalDate.of(anioFin, mesFin, 1).withDayOfMonth(LocalDate.of(anioFin, mesFin, 1).lengthOfMonth());

        List<ReservaDTO> reservasProcesables = todasLasReservas.stream()
                .filter(r -> "PAGADA".equalsIgnoreCase(r.getEstadoReserva()))
                .filter(r -> r.getFechaHora() != null && r.getMontoFinal() != null && r.getMontoFinal() > 0) // Ensure essential data
                .filter(r -> {
                    LocalDate fechaReserva = r.getFechaHora().toLocalDate();
                    return !fechaReserva.isBefore(fechaInicioReporte) && !fechaReserva.isAfter(fechaFinReporte);
                })
                .toList();

        List<String> mesesDelRango = obtenerMesesEnRango(fechaInicioReporte, fechaFinReporte);

        Map<String, Map<String, Double>> ingresosPorRangoPersonasYMes = reservasProcesables.stream()
                .filter(r -> getRangoPersonas(r.getCantidadPersonas()) != null)
                .collect(Collectors.groupingBy(
                        r -> getRangoPersonas(r.getCantidadPersonas()),
                        Collectors.groupingBy(
                                r -> YearMonth.from(r.getFechaHora().toLocalDate()).format(MONTH_FORMATTER),
                                Collectors.summingDouble(ReservaDTO::getMontoFinal)
                        )
                ));

        List<ReporteFilaDTO> filasReporte = new ArrayList<>();
        // RANGOS_PERSONAS_DEFINIDOS are fixed as per requirement
        for (String rangoPersonas : RANGOS_PERSONAS_DEFINIDOS) {
            Map<String, Double> ingresosMes = new LinkedHashMap<>();
            double totalCategoria = 0.0;
            for (String mes : mesesDelRango) {
                double ingresoMes = ingresosPorRangoPersonasYMes.getOrDefault(rangoPersonas, Collections.emptyMap()).getOrDefault(mes, 0.0);
                ingresosMes.put(mes, ingresoMes);
                totalCategoria += ingresoMes;
            }
            filasReporte.add(new ReporteFilaDTO(rangoPersonas, ingresosMes, totalCategoria));
        }
        return construirReporteResponse(filasReporte, mesesDelRango);
    }

    private ReporteResponseDTO construirReporteResponse(List<ReporteFilaDTO> filasReporte, List<String> mesesDelRango) {
        Map<String, Double> totalesPorMes = new LinkedHashMap<>(); // Use LinkedHashMap to maintain month order
        double granTotal = 0.0;

        for (String mes : mesesDelRango) {
            double totalMesColumna = 0.0;
            for (ReporteFilaDTO fila : filasReporte) {
                totalMesColumna += fila.getIngresosPorMes().getOrDefault(mes, 0.0);
            }
            totalesPorMes.put(mes, totalMesColumna);
        }
        // Calculate grand total from the sum of category totals for accuracy
        granTotal = filasReporte.stream().mapToDouble(ReporteFilaDTO::getTotalIngresosCategoria).sum();

        return new ReporteResponseDTO(mesesDelRango, filasReporte, totalesPorMes, granTotal);
    }

    private List<String> obtenerMesesEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<String> meses = new ArrayList<>();
        YearMonth mesIterador = YearMonth.from(fechaInicio);
        YearMonth mesFinal = YearMonth.from(fechaFin);

        while (!mesIterador.isAfter(mesFinal)) {
            meses.add(mesIterador.format(MONTH_FORMATTER));
            mesIterador = mesIterador.plusMonths(1);
        }
        return meses;
    }

    private String getRangoPersonas(int cantidadPersonas) {
        if (cantidadPersonas >= 1 && cantidadPersonas <= 2) {
            return "1-2 Personas";
        } else if (cantidadPersonas >= 3 && cantidadPersonas <= 5) {
            return "3-5 Personas";
        } else if (cantidadPersonas >= 6 && cantidadPersonas <= 10) {
            return "6-10 Personas";
        } else if (cantidadPersonas >= 11 && cantidadPersonas <= 15) {
            return "11-15 Personas";
        }
        return null;
    }
}