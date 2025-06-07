package com.example.ms_registroreserva_comprobantepago.services;

import com.example.ms_registroreserva_comprobantepago.entities.ComprobanteEntity;
import com.example.ms_registroreserva_comprobantepago.entities.ReservaEntity;
import com.example.ms_registroreserva_comprobantepago.repositories.ComprobanteRepository;
import com.example.ms_registroreserva_comprobantepago.repositories.ReservaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

// PDF generation imports
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ComprobanteService {

    private static final Logger logger = LoggerFactory.getLogger(ComprobanteService.class);

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService; // To update reserva state

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    private static final double IVA_PERCENTAGE = 0.19;

    public List<ComprobanteEntity> getAllComprobantes() {
        return comprobanteRepository.findAll();
    }

    public Optional<ComprobanteEntity> getComprobanteById(Long id) {
        return comprobanteRepository.findById(id);
    }

    public Optional<ComprobanteEntity> getComprobanteByIdReserva(Long idReserva) {
        return comprobanteRepository.findByIdReserva(idReserva);
    }

    public Optional<ComprobanteEntity> getComprobanteByCodigo(String codigo) {
        return comprobanteRepository.findByCodigoComprobante(codigo);
    }

    @Transactional
    public ComprobanteEntity crearYGuardarComprobante(Long idReserva, String metodoPago) {
        ReservaEntity reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + idReserva));

        if (!"PENDIENTE".equals(reserva.getEstadoReserva()) && !"CONFIRMADA".equals(reserva.getEstadoReserva())) {
            throw new IllegalStateException("Solo se puede generar comprobante para reservas PENDIENTES o CONFIRMADAS. Estado actual: " + reserva.getEstadoReserva());
        }
        if (comprobanteRepository.findByIdReserva(idReserva).isPresent()) {
            throw new IllegalStateException("Ya existe un comprobante para la reserva ID: " + idReserva);
        }

        ComprobanteEntity comprobante = new ComprobanteEntity();
        String codigoComprobante = "KRM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        comprobante.setCodigoComprobante(codigoComprobante);
        comprobante.setIdReserva(idReserva);
        comprobante.setFechaEmision(LocalDateTime.now());

        comprobante.setRutUsuario(reserva.getRutUsuario());
        comprobante.setEmailUsuario(reserva.getEmailUsuario());
        comprobante.setNombreUsuario(reserva.getNombreUsuario());

        comprobante.setMontoBase(reserva.getMontoBase());
        comprobante.setPorcentajeDescuentoAplicado(reserva.getPorcentajeDescuentoAplicado());
        comprobante.setMontoDescuentoTotal(reserva.getMontoDescuento());

        double subtotalSinIva = reserva.getMontoFinal();
        comprobante.setSubtotalSinIva(subtotalSinIva);

        double ivaCalculado = subtotalSinIva * IVA_PERCENTAGE;
        comprobante.setIva(ivaCalculado);
        comprobante.setMontoPagadoTotal(subtotalSinIva + ivaCalculado);

        comprobante.setMetodoPago(metodoPago);
        comprobante.setEstadoPago("PAGADO");

        ComprobanteEntity comprobanteGuardado = comprobanteRepository.save(comprobante);
        logger.info("Comprobante {} creado y guardado para reserva {}", codigoComprobante, idReserva);

        // Actualizar estado de la reserva a PAGADA
        reservaService.actualizarEstadoReserva(idReserva, "PAGADA");

        return comprobanteGuardado;
    }

    public byte[] generarPdfBytesParaComprobante(String codigoComprobante) {
        ComprobanteEntity comprobante = comprobanteRepository.findByCodigoComprobante(codigoComprobante)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado con código: " + codigoComprobante));
        ReservaEntity reserva = reservaRepository.findById(comprobante.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva asociada (" + comprobante.getIdReserva() + ") no encontrada para comprobante: " + codigoComprobante));

        logger.info("Generando PDF para comprobante {}", codigoComprobante);
        return generarPdfHelper(comprobante, reserva); // Call the helper
    }

    public byte[] generarPdfBytesParaComprobantePorId(Long idComprobante) {
        ComprobanteEntity comprobante = comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado con ID: " + idComprobante));
        return generarPdfBytesParaComprobante(comprobante.getCodigoComprobante());
    }

    @Transactional // Potentially, if you log the attempt or update a "last_sent_email_attempt" field
    public void enviarEmailConAdjuntoPdf(String codigoComprobante) {
        ComprobanteEntity comprobante = comprobanteRepository.findByCodigoComprobante(codigoComprobante)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado con código: " + codigoComprobante));
        ReservaEntity reserva = reservaRepository.findById(comprobante.getIdReserva())
                .orElseThrow(() -> new RuntimeException("Reserva asociada (" + comprobante.getIdReserva() + ") no encontrada para comprobante: " + codigoComprobante));

        if (javaMailSender == null) {
            logger.warn("JavaMailSender no está configurado. No se puede enviar el email para el comprobante {}.", codigoComprobante);
            throw new IllegalStateException("Servicio de correo no configurado para enviar email.");
        }

        logger.info("Preparando email con PDF para comprobante {}", codigoComprobante);
        byte[] pdfBytes = generarPdfHelper(comprobante, reserva); // Regenerate or fetch if stored
        enviarEmailHelper(reserva.getEmailUsuario(), pdfBytes, codigoComprobante); // Call the helper
    }

    // --- Helper methods (previously part of the combined method) ---

    private byte[] generarPdfHelper(ComprobanteEntity comprobante, ReservaEntity reserva) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(36, 36, 36, 36);
            Locale localeChile = new Locale("es", "CL");

            Paragraph header = new Paragraph()
                    .add(new Text("Comprobante de Reserva - KartingRM").setBold().setFontSize(16))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(header);

            Paragraph codigoReservaPar = new Paragraph() // Renamed to avoid conflict
                    .add(new Text("Código de Comprobante: " + comprobante.getCodigoComprobante()).setBold().setFontSize(14))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(codigoReservaPar);

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(15);

            addInfoRow(infoTable, "Nombre Cliente:", reserva.getNombreUsuario());
            addInfoRow(infoTable, "Email:", reserva.getEmailUsuario());
            addInfoRow(infoTable, "Teléfono:", reserva.getTelefonoUsuario() != null ? reserva.getTelefonoUsuario() : "No registrado");
            addInfoRow(infoTable, "RUT Cliente:", reserva.getRutUsuario() != null ? reserva.getRutUsuario() : "No registrado");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaInicio = reserva.getFechaHora() != null ? reserva.getFechaHora().format(formatter) : "N/A";
            LocalDateTime fechaFinDateTime = reserva.getFechaHora() != null ? reserva.getFechaHora().plusHours(reserva.getDuracionHoras()) : null;
            String fechaFin = fechaFinDateTime != null ? fechaFinDateTime.format(formatter) : "N/A";

            addInfoRow(infoTable, "Fecha y Hora:", fechaInicio + " a " + fechaFin);
            addInfoRow(infoTable, "Duración:", reserva.getDuracionHoras() + " horas");
            addInfoRow(infoTable, "Número de Personas:", String.valueOf(reserva.getCantidadPersonas()));

            String tipoReservaStr = switch (reserva.getTipoReserva()) {
                case 1 -> "Normal";
                case 2 -> "Extendida";
                case 3 -> "Premium";
                default -> "Desconocido (" + reserva.getTipoReserva() + ")";
            };
            addInfoRow(infoTable, "Tipo de Reserva:", tipoReservaStr);

            if (reserva.getCantidadCumple() > 0) {
                addInfoRow(infoTable, "Personas de Cumpleaños:", String.valueOf(reserva.getCantidadCumple()));
            }
            document.add(infoTable);

            if (comprobante.getMontoDescuentoTotal() != null && comprobante.getMontoDescuentoTotal() > 0) {
                Paragraph notaDescuento = new Paragraph()
                        .add(new Text("Se ha aplicado el descuento más favorable para usted.").setItalic())
                        .setMarginBottom(10);
                document.add(notaDescuento);
            }

            document.add(new Paragraph("Detalle de Precios:").setBold().setMarginBottom(5));
            Table pricingTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            pricingTable.addHeaderCell(createHeaderCell("Concepto"));
            pricingTable.addHeaderCell(createHeaderCell("Valor (CLP)"));

            addPricingRow(pricingTable, "Precio Base", comprobante.getMontoBase().floatValue(), localeChile);

            if (comprobante.getMontoDescuentoTotal() != null && comprobante.getMontoDescuentoTotal() > 0) {
                addPricingRow(pricingTable, "Descuento Aplicado (" + String.format("%.0f", comprobante.getPorcentajeDescuentoAplicado()) + "%)", -comprobante.getMontoDescuentoTotal().floatValue(), localeChile);
            }

            addPricingRow(pricingTable, "Subtotal", comprobante.getSubtotalSinIva().floatValue(), localeChile);
            addPricingRow(pricingTable, "IVA (19%)", comprobante.getIva().floatValue(), localeChile);

            Cell totalLabelCell = new Cell(1, 1).add(new Paragraph("TOTAL A PAGAR").setBold()).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1)).setBorderBottom(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPaddingTop(5);
            pricingTable.addFooterCell(totalLabelCell);
            Cell totalValueCell = new Cell(1, 1).add(new Paragraph(String.format(localeChile, "$%,.0f", comprobante.getMontoPagadoTotal())).setBold()).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1)).setBorderBottom(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPaddingTop(5);
            pricingTable.addFooterCell(totalValueCell);
            document.add(pricingTable);

            Paragraph footer = new Paragraph().add(new Text("Este comprobante debe ser presentado el día de su reserva en el Kartódromo.\n").setItalic()).add(new Text("¡Gracias por preferir KartingRM! Te esperamos.").setItalic()).setTextAlignment(TextAlignment.CENTER).setFontSize(10).setMarginTop(20);
            document.add(footer);

            // Ensure document is properly closed
            document.close();
            pdfDoc.close();
            writer.close();

            // Get bytes after everything is closed
            byte[] pdfBytes = outputStream.toByteArray();
            outputStream.close();

            logger.info("PDF generado con éxito (helper) para comprobante código: {} - Tamaño: {} bytes", comprobante.getCodigoComprobante(), pdfBytes.length);
            return pdfBytes;

        } catch (Exception e) {
            logger.error("Error al generar el PDF (helper) para comprobante {}: {}", comprobante.getCodigoComprobante(), e.getMessage(), e);
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }

    private void enviarEmailHelper(String email, byte[] pdfBytes, String codigoComprobante) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("🏎️ Comprobante de Reserva - KartingRM [" + codigoComprobante + "]");
            helper.setText("Estimado cliente,\n\nAdjunto encontrará el comprobante de su reserva en KartingRM con el código " + codigoComprobante + ".\n\nRecuerde presentar este comprobante el día de su visita al Kartódromo.\n\nMuchas gracias por su preferencia.\n\nAtentamente,\nEl equipo de KartingRM");
            helper.addAttachment("Comprobante-" + codigoComprobante + ".pdf", new ByteArrayResource(pdfBytes));

            javaMailSender.send(message);
            logger.info("Email enviado con éxito (helper) a: {} para comprobante {}", email, codigoComprobante);

        } catch (Exception e) {
            logger.error("Error al enviar el email (helper) para comprobante {}: {}", codigoComprobante, e.getMessage(), e);
            // Not rethrowing to avoid breaking a larger process if this is called as part of one.
            // The calling method can decide if the overall operation failed.
        }
    }

    // --- Unchanged helper methods for PDF table construction ---
    private void addInfoRow(Table table, String label, String value) {
        Cell labelCell = new Cell().add(new Paragraph(new Text(label).setBold())).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setPaddingRight(10);
        table.addCell(labelCell);
        Cell valueCell = new Cell().add(new Paragraph(value != null ? value : "-")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        table.addCell(valueCell);
    }

    private void addPricingRow(Table table, String description, float value, Locale locale) {
        Cell descCell = new Cell().add(new Paragraph(description)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        table.addCell(descCell);
        String formattedValue = String.format(locale, "$%,.0f", value);
        Cell valueCell = new Cell().add(new Paragraph(formattedValue)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(valueCell);
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1));
    }
}