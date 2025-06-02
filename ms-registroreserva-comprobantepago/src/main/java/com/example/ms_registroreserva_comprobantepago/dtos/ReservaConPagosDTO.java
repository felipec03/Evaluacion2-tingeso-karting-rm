package com.example.ms_registroreserva_comprobantepago.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ReservaConPagosDTO extends ReservaDTO { // Extiende ReservaDTO para heredar sus campos
    private List<ComprobantePagoDTO> pagos;
}