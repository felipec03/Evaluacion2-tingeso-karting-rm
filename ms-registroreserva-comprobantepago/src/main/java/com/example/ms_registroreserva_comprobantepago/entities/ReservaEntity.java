package com.example.ms_registroreserva_comprobantepago.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteId; // ID del cliente (puede ser un UUID o email)
    private String emailCliente; // Email para notificaciones y comprobante
    private String nombreCliente; // Nombre para el comprobante

    private LocalDateTime inicioReserva;
    private LocalDateTime finReserva;
    private LocalDate fechaReserva; // Denormalizado de inicioReserva para queries
    private int duracionHoras;

    private Date cumpleaniosCliente; // Fecha de cumpleaños del cliente (si aplica para descuento)
    private int numeroPersonas;
    private int tipoReserva; // Mapea a los tipos definidos (1: Adulto, 2: Niño, 3: Mixta)
    private int cantidadPersonasCumpleanos; // Para descuento de cumpleaños

    private float precioBaseCalculado;
    private float descuentoGrupoAplicado;
    private float descuentoClienteFrecuenteAplicado;
    private float descuentoCumpleanosAplicado;
    private float subtotal; // precioBase - descuentos
    private float ivaCalculado;
    private float totalAPagar;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private Long comprobanteId; // ID del comprobante asociado, una vez generado

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}