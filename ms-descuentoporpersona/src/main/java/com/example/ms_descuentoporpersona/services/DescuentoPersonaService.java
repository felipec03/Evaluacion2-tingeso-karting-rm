package com.example.ms_descuentoporpersona.services;

import com.example.ms_descuentoporpersona.entities.DescuentoPersonaEntity;
import com.example.ms_descuentoporpersona.repositories.DescuentoPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class DescuentoPersonaService {
    @Autowired
    DescuentoPersonaRepository descuentoPersonaRepository;

    public List<DescuentoPersonaEntity> obtenerTodosDescuentos() {
        return descuentoPersonaRepository.findAll();
    }

    public List<DescuentoPersonaEntity> obtenerDescuentosActivos() {
        return descuentoPersonaRepository.findByActivoTrue();
    }

    public DescuentoPersonaEntity guardarDescuento(DescuentoPersonaEntity descuento) {
        return descuentoPersonaRepository.save(descuento);
    }

    public DescuentoPersonaEntity obtenerDescuentoPorId(Long id) {
        return descuentoPersonaRepository.findById(id).orElse(null);
    }

    public DescuentoPersonaEntity actualizarDescuento(DescuentoPersonaEntity descuento) {
        return descuentoPersonaRepository.save(descuento);
    }

    public boolean eliminarDescuento(Long id) throws Exception {
        try {
            descuentoPersonaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public double calcularDescuentoPorPersonas(int numeroPersonas) {
        List<DescuentoPersonaEntity> descuentos = descuentoPersonaRepository.findDescuentoByPersonas(numeroPersonas);

        if (descuentos.isEmpty()) {
            return 0.0;
        }

        // Retorna el descuento aplicable para el número de personas
        return descuentos.get(0).getPorcentajeDescuento();
    }

    public double aplicarDescuentoPorPersonas(double montoBase, int numeroPersonas) {
        double porcentajeDescuento = calcularDescuentoPorPersonas(numeroPersonas);
        if (porcentajeDescuento > 0) {
            return montoBase * (porcentajeDescuento / 100);
        }
        return 0.0;
    }

    // Método para inicializar datos por defecto según la rúbrica
    @PostConstruct
    public void inicializarDescuentosPorDefecto() {
        if (descuentoPersonaRepository.count() == 0) {
            // 1-2 personas: 0% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 1, 2, 0.0, true));
            // 3-5 personas: 10% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 3, 5, 10.0, true));
            // 6-10 personas: 20% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 6, 10, 20.0, true));
            // 11-15 personas: 30% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 11, 15, 30.0, true));

            System.out.println("Descuentos por persona inicializados correctamente");
        }
    }
}