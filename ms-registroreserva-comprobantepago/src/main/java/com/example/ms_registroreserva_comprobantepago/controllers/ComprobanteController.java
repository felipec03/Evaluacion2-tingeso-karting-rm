package com.example.ms_registroreserva_comprobantepago.controllers;

// Assuming you created this DTO

import com.example.ms_registroreserva_comprobantepago.entities.ComprobanteEntity;
import com.example.ms_registroreserva_comprobantepago.services.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Removed Map import as we'll use a DTO for the request body

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;

    @GetMapping("/")
    public ResponseEntity<List<ComprobanteEntity>> getAllComprobantes() {
        List<ComprobanteEntity> comprobantes = comprobanteService.getAllComprobantes();
        return ResponseEntity.ok(comprobantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComprobanteEntity> getComprobanteById(@PathVariable Long id) {
        return comprobanteService.getComprobanteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<ComprobanteEntity> getComprobanteByIdReserva(@PathVariable Long idReserva) {
        return comprobanteService.getComprobanteByIdReserva(idReserva)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoComprobante}")
    public ResponseEntity<ComprobanteEntity> getComprobanteByCodigo(@PathVariable String codigoComprobante) {
        return comprobanteService.getComprobanteByCodigo(codigoComprobante)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to create the Comprobante entity data
    @PostMapping("/crear/reserva/{idReserva}")
    public ResponseEntity<?> crearComprobante(@PathVariable Long idReserva, @RequestParam String metodoPago) { // Modificado aquí
        try {
            // Asegúrate que metodoPago no esté vacío si es necesario, puedes añadir validación.
            if (metodoPago == null || metodoPago.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El parámetro 'metodoPago' es requerido.");
            }
            ComprobanteEntity comprobante = comprobanteService.crearYGuardarComprobante(idReserva, metodoPago); // Modificado aquí
            return new ResponseEntity<>(comprobante, HttpStatus.CREATED);
        } catch (IllegalStateException e) { // Handles "already exists" or "invalid state"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) { // Handles "Reserva no encontrada" or other general issues
            if (e.getMessage() != null && e.getMessage().startsWith("Reserva no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to get the PDF for an existing comprobante
    @GetMapping("/id/{idComprobante}/pdf")
    public ResponseEntity<byte[]> descargarPdfComprobantePorId(@PathVariable Long idComprobante) {
        try {
            byte[] pdfBytes = comprobanteService.generarPdfBytesParaComprobantePorId(idComprobante);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Change from inline to attachment to force download
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Comprobante-" + idComprobante + ".pdf");
            // Add content length header
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Error handling remains the same
            if (e.getMessage() != null && (e.getMessage().contains("Comprobante no encontrado") || e.getMessage().contains("Reserva asociada no encontrada"))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint to trigger sending an email with the PDF for an existing comprobante
    @PostMapping("/{codigoComprobante}/enviar-email")
    public ResponseEntity<String> enviarEmailComprobante(@PathVariable String codigoComprobante) {
        try {
            comprobanteService.enviarEmailConAdjuntoPdf(codigoComprobante);
            return ResponseEntity.ok("Solicitud para enviar email para el comprobante " + codigoComprobante + " procesada.");
        } catch (IllegalStateException e) { // For mail service not configured or other specific logical errors
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (RuntimeException e) { // For comprobante/reserva not found
            if (e.getMessage() != null && (e.getMessage().contains("Comprobante no encontrado") || e.getMessage().contains("Reserva asociada no encontrada"))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud de envío de email: " + e.getMessage());
        }
    }
}