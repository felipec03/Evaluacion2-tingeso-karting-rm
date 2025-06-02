package com.example.ms_tarifasconfig.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeriadoDTO {
    private Long id;
    private String fecha; // "MM-dd"
    private String descripcion;
}