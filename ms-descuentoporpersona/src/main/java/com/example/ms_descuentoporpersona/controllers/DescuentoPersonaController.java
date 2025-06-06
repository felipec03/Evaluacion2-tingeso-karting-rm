package com.example.ms_descuentoporpersona.controllers;

import com.example.ms_descuentoporpersona.entities.DescuentoPersonaEntity;
import com.example.ms_descuentoporpersona.services.DescuentoPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/descuento-persona")
@CrossOrigin("*")
public class DescuentoPersonaController {

    @Autowired
    DescuentoPersonaService descuentoPersonaService;

    @GetMapping("/")
    public ResponseEntity<List<DescuentoPersonaEntity>> listarDescuentos() {
        List<DescuentoPersonaEntity> descuentos = descuentoPersonaService.obtenerTodosDescuentos();
        return ResponseEntity.ok(descuentos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<DescuentoPersonaEntity>> listarDescuentosActivos() {
        List<DescuentoPersonaEntity> descuentos = descuentoPersonaService.obtenerDescuentosActivos();
        return ResponseEntity.ok(descuentos);
    }

    @PostMapping("/")
    public ResponseEntity<DescuentoPersonaEntity> guardarDescuento(@RequestBody DescuentoPersonaEntity descuento) {
        DescuentoPersonaEntity descuentoNuevo = descuentoPersonaService.guardarDescuento(descuento);
        return ResponseEntity.ok(descuentoNuevo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DescuentoPersonaEntity> obtenerDescuentoPorId(@PathVariable Long id) {
        DescuentoPersonaEntity descuento = descuentoPersonaService.obtenerDescuentoPorId(id);
        return ResponseEntity.ok(descuento);
    }

    @PutMapping("/")
    public ResponseEntity<DescuentoPersonaEntity> actualizarDescuento(@RequestBody DescuentoPersonaEntity descuento) {
        DescuentoPersonaEntity descuentoActualizado = descuentoPersonaService.actualizarDescuento(descuento);
        return ResponseEntity.ok(descuentoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminarDescuentoPorId(@PathVariable Long id) throws Exception {
        descuentoPersonaService.eliminarDescuento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/calcular/{numeroPersonas}")
    public ResponseEntity<Double> calcularDescuentoPorPersonas(@PathVariable int numeroPersonas) {
        double porcentajeDescuento = descuentoPersonaService.calcularDescuentoPorPersonas(numeroPersonas);
        return ResponseEntity.ok(porcentajeDescuento);
    }

    @GetMapping("/aplicar")
    public ResponseEntity<Double> aplicarDescuentoPorPersonas(
            @RequestParam double montoBase,
            @RequestParam int numeroPersonas) {
        double descuentoAplicado = descuentoPersonaService.aplicarDescuentoPorPersonas(montoBase, numeroPersonas);
        return ResponseEntity.ok(descuentoAplicado);
    }

    // Ya no es necesario este endpoint porque usamos @PostConstruct
    // pero lo dejamos por compatibilidad con código existente
    @PostMapping("/inicializar")
    public ResponseEntity<String> inicializarDescuentos() {
        descuentoPersonaService.inicializarDescuentosPorDefecto();
        return ResponseEntity.ok("Descuentos por defecto inicializados correctamente");
    }
}