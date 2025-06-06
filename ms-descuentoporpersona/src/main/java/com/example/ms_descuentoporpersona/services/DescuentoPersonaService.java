package com.example.ms_descuentoporpersona.services;

import com.example.ms_descuentoporpersona.entities.DescuentoPersonaEntity;
import com.example.ms_descuentoporpersona.repositories.DescuentoPersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // Retorna el descuento con el mayor número de personas que sea menor o igual al número dado
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
    public void inicializarDescuentosPorDefecto() {
        if (descuentoPersonaRepository.count() == 0) {
            // 2-5 personas: 3% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 2, 3.0, true));
            // 6-9 personas: 8% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 6, 8.0, true));
            // 10 o más personas: 15% descuento
            guardarDescuento(new DescuentoPersonaEntity(null, 10, 15.0, true));
        }
    }
}
