package com.example.ms_registroreserva_comprobantepago.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComprobante;

    @Column(unique = true, nullable = false)
    private String codigoComprobante; // Ejemplo: KRM-XYZ123

    private Long idReserva; // FK a ReservaEntity.id

    private LocalDateTime fechaEmision;

    private String rutUsuario;
    private String emailUsuario;
    private String nombreUsuario;

    private Double montoBase;
    private Double porcentajeDescuentoAplicado; // El porcentaje que se aplicó
    private Double montoDescuentoTotal; // El monto total descontado
    // Podrías añadir más desglose de descuentos si M4 los proveyera y quisieras persistirlos aquí
    // private Double descuentoPorPersonas;
    // private Double descuentoClienteFrecuente;
    // private Double descuentoDiaEspecial;
    private Double subtotalSinIva; // montoBase - montoDescuentoTotal
    private Double iva; // Asumimos un IVA estándar, ej. 19%
    private Double montoPagadoTotal; // subtotalSinIva + iva

    private String metodoPago; // TARJETA, EFECTIVO, TRANSFERENCIA
    private String estadoPago; // PAGADO
}