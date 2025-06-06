package com.example.ms_descuentosclientefrecuente.services;

import com.example.ms_descuentosclientefrecuente.entities.DescuentoFrecuenteEntity;
import com.example.ms_descuentosclientefrecuente.repositories.DescuentoFrecuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DescuentoFrecuenteService {

    @Autowired
    private DescuentoFrecuenteRepository descuentoRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Calcula el descuento para un cliente en un mes específico
     */
    public Double calcularDescuento(String rutCliente, Integer mes, Integer anio) {
        // Obtener la cantidad de visitas del cliente en el mes indicado
        Integer cantidadVisitas = obtenerCantidadVisitas(rutCliente, mes, anio);

        // Calcular el porcentaje de descuento según las reglas
        Double porcentajeDescuento = determinarPorcentajeDescuento(cantidadVisitas);

        // Guardar o actualizar el registro de descuento
        guardarDescuento(rutCliente, mes, anio, cantidadVisitas, porcentajeDescuento);

        return porcentajeDescuento;
    }

    /**
     * Obtiene la cantidad de visitas de un cliente en un mes específico
     * Consulta el servicio de registros de visitas
     */
    private Integer obtenerCantidadVisitas(String rutCliente, Integer mes, Integer anio) {
        try {
            // Llamar al servicio de visitas para obtener los registros
            String url = "http://registro-visitas-service/visitas/cliente/" + rutCliente + "?mes=" + mes + "&anio=" + anio;
            Object[] visitas = restTemplate.getForObject(url, Object[].class);

            // Contar la cantidad de visitas
            return visitas != null ? visitas.length : 0;
        } catch (Exception e) {
            // En caso de error, registrarlo y devolver 0
            System.out.println("Error al obtener visitas: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Determina el porcentaje de descuento según la cantidad de visitas
     */
    private Double determinarPorcentajeDescuento(Integer cantidadVisitas) {
        if (cantidadVisitas >= 7) {
            return 0.30; // Muy frecuente: 30% de descuento
        } else if (cantidadVisitas >= 5) {
            return 0.20; // Frecuente: 20% de descuento
        } else if (cantidadVisitas >= 2) {
            return 0.10; // Regular: 10% de descuento
        } else {
            return 0.0; // No frecuente: 0% de descuento
        }
    }

    /**
     * Guarda o actualiza el registro de descuento en la base de datos
     */
    private void guardarDescuento(String rutCliente, Integer mes, Integer anio, Integer cantidadVisitas, Double porcentajeDescuento) {
        Optional<DescuentoFrecuenteEntity> descuentoExistente = descuentoRepository.findByRutClienteAndMesAndAnio(rutCliente, mes, anio);

        DescuentoFrecuenteEntity descuento;
        if (descuentoExistente.isPresent()) {
            descuento = descuentoExistente.get();
        } else {
            descuento = new DescuentoFrecuenteEntity();
            descuento.setRutCliente(rutCliente);
            descuento.setMes(mes);
            descuento.setAnio(anio);
        }

        descuento.setCantidadVisitas(cantidadVisitas);
        descuento.setPorcentajeDescuento(porcentajeDescuento);

        descuentoRepository.save(descuento);
    }

    /**
     * Obtiene el descuento actual para un cliente en el mes actual
     */
    public Double obtenerDescuentoActual(String rutCliente) {
        LocalDate fechaActual = LocalDate.now();
        return calcularDescuento(rutCliente, fechaActual.getMonthValue(), fechaActual.getYear());
    }
}