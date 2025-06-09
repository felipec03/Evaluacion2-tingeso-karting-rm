package com.example.ms_racksemanal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RackCeldaDTO {
    private boolean reservado;
    private Long reservaId;
}
