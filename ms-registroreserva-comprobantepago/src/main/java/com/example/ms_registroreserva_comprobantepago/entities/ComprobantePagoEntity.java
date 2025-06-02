package com.example.ms_registroreserva_comprobantepago.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantePagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long reservaId; // FK a la reserva

    private String clienteId;
    private String emailCliente;
    private String nombreCliente; // Snapshot al momento de generar comprobante

    private String codigoComprobante; // Ej: KRM-XYZ123

    private float tarifaBase;
    private float descuentoGrupo;
    private float descuentoClienteFrecuente;
    private float descuentoCumpleanos;
    private float precioSinIva; // subtotal
    private float iva;
    private float totalPagado;

    private LocalDateTime fechaEmision;
    private String metodoPagoUtilizado; // Ej: "TARJETA_CREDITO", "TRANSFERENCIA"
    private String referenciaTransaccionPago; // ID de la transacción del gateway de pago

    // Campos de la reserva para el comprobante
    private LocalDateTime inicioReserva;
    private LocalDateTime finReserva;
    private int duracionHorasReserva;
    private int numeroPersonasReserva;
    private String tipoReservaDescripcion; // Ej: "Adulto", "Niño", "Mixta"
    private int cantidadPersonasCumpleanosReserva;

    @Lob // Para almacenar el PDF como byte array si se decide así, o usar URL
    private byte[] pdfComprobante;
    private String urlPdfComprobante; // Alternativa si se almacena en S3 u otro storage

    @PrePersist
    protected void onCreate() {
        fechaEmision = LocalDateTime.now();
    }
}