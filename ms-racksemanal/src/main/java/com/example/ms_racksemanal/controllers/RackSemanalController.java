package com.example.ms_racksemanal.controllers;

import com.example.ms_racksemanal.model.Reserva;
import com.example.ms_racksemanal.services.RackSemanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rack-semanal")
public class RackSemanalController {

    @Autowired
    private RackSemanalService rackSemanalService;

    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = rackSemanalService.obtenerTodasLasReservasActivas();
        return ResponseEntity.ok(reservas);
    }
}