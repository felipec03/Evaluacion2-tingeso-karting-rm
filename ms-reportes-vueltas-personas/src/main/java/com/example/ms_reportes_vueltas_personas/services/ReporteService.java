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
    private static final List<String> RANGOS_PERSONAS_DEFINIDOS = List.of("1-3 Personas", "4-6 Personas", "7-9 Personas"); // Y más si es necesario

    public ReporteResponseDTO generarReporteIngresosPorTarifa(int anioInicio, int mesInicio, int anioFin, int mesFin) {
        List<ReservaDTO> todasLasReservas = reservaFeignClient.obtenerTodasLasReservas();
        if (todasLasReservas == null) {
            todasLasReservas = new ArrayList<>(); // Evitar NullPointerException
        }

        LocalDate fechaInicioReporte = LocalDate.of(anioInicio, mesInicio, 1);
        LocalDate fechaFinReporte = LocalDate.of(anioFin, mesFin, 1).withDayOfMonth(LocalDate.of(anioFin, mesFin, 1).lengthOfMonth());

        List<ReservaDTO> reservasFiltradas = todasLasReservas.stream()
                .filter(r -> r.getFechaReserva() != null &&
                        !r.getFechaReserva().isBefore(fechaInicioReporte) &&
                        !r.getFechaReserva().isAfter(fechaFinReporte))
                .collect(Collectors.toList());

        List<String> mesesDelRango = obtenerMesesEnRango(fechaInicioReporte, fechaFinReporte);

        // Agrupar por tipo de tarifa y luego por mes
        Map<String, Map<String, Double>> ingresosPorTarifaYMes = reservasFiltradas.stream()
                .collect(Collectors.groupingBy(
                        ReservaDTO::getTipoTarifa,
                        Collectors.groupingBy(
                                r -> YearMonth.from(r.getFechaReserva()).format(MONTH_FORMATTER),
                                Collectors.summingDouble(ReservaDTO::getMontoTotal)
                        )
                ));

        // Obtener todos los tipos de tarifa únicos de las reservas filtradas
        // Para cumplir "Todos los tipos ... definidos en el sistema se deben mostrar":
        // Si hay tipos de tarifa que no tuvieron reservas, no aparecerán aquí.
        // Una mejora sería tener una lista de todos los tipos de tarifa posibles (desde config o BD).
        Set<String> todosLosTiposTarifa = reservasFiltradas.stream()
                .map(ReservaDTO::getTipoTarifa)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // Si se quiere asegurar que todos los tipos de tarifa definidos aparezcan, incluso con 0:
        // Set<String> todosLosTiposTarifa = new HashSet<>(Arrays.asList("10 Vueltas", "15 Vueltas", "20 Vueltas", "30 Minutos", "60 Minutos"));
        // O obtenerlos de una configuración o de otro endpoint.

        List<ReporteFilaDTO> filasReporte = new ArrayList<>();
        for (String tipoTarifa : todosLosTiposTarifa.stream().sorted().collect(Collectors.toList())) { // Ordenar alfabéticamente
            Map<String, Double> ingresosMes = new LinkedHashMap<>(); // Usar LinkedHashMap para mantener el orden de los meses
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

        List<ReservaDTO> reservasFiltradas = todasLasReservas.stream()
                .filter(r -> r.getFechaReserva() != null &&
                        !r.getFechaReserva().isBefore(fechaInicioReporte) &&
                        !r.getFechaReserva().isAfter(fechaFinReporte))
                .collect(Collectors.toList());

        List<String> mesesDelRango = obtenerMesesEnRango(fechaInicioReporte, fechaFinReporte);

        // Agrupar por rango de personas y luego por mes
        Map<String, Map<String, Double>> ingresosPorRangoPersonasYMes = reservasFiltradas.stream()
                .filter(r -> getRangoPersonas(r.getNumeroPersonas()) != null) // Filtrar si no cae en ningún rango
                .collect(Collectors.groupingBy(
                        r -> getRangoPersonas(r.getNumeroPersonas()),
                        Collectors.groupingBy(
                                r -> YearMonth.from(r.getFechaReserva()).format(MONTH_FORMATTER),
                                Collectors.summingDouble(ReservaDTO::getMontoTotal)
                        )
                ));

        List<ReporteFilaDTO> filasReporte = new ArrayList<>();
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
        Map<String, Double> totalesPorMes = new LinkedHashMap<>();
        double granTotal = 0.0;

        for (String mes : mesesDelRango) {
            double totalMes = 0.0;
            for (ReporteFilaDTO fila : filasReporte) {
                totalMes += fila.getIngresosPorMes().getOrDefault(mes, 0.0);
            }
            totalesPorMes.put(mes, totalMes);
            granTotal += totalMes;
        }
        // El granTotal también se puede calcular sumando los totales de categoría
        // granTotal = filasReporte.stream().mapToDouble(ReporteFilaDTO::getTotalIngresosCategoria).sum();

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

    private String getRangoPersonas(int numeroPersonas) {
        if (numeroPersonas >= 1 && numeroPersonas <= 3) {
            return "1-3 Personas";
        } else if (numeroPersonas >= 4 && numeroPersonas <= 6) {
            return "4-6 Personas";
        } else if (numeroPersonas >= 7 && numeroPersonas <= 9) {
            return "7-9 Personas";
        }
        // Añadir más rangos si es necesario
        // else if (numeroPersonas >= 10 && numeroPersonas <= 12) {
        //     return "10-12 Personas";
        // }
        return null; // O un rango "Otros" si se prefiere
    }
}
