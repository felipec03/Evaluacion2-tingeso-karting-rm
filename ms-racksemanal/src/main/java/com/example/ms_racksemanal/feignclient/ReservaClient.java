package com.example.ms_racksemanal.feignclient;

import com.example.ms_racksemanal.model.Reserva;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-registroreserva-comprobantepago", path = "/reserva")
public interface ReservaClient {

    @GetMapping
    ResponseEntity<List<Reserva>> getAllReservas();

    @GetMapping("/{id}")
    ResponseEntity<Reserva> getReservaById(@PathVariable("id") Long id);
}
