package com.example.ms_racksemanal.feignclient;

import com.example.ms_racksemanal.model.Reserva;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-registroreserva-comprobantepago", path = "/api/reservas") // Ensure this path is correct
public interface ReservaClient {

    @GetMapping("/") // Ensure this endpoint in ms-registroreserva-comprobantepago returns List<ReservaEntity>
    ResponseEntity<List<Reserva>> getAllReservas();
}