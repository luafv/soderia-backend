package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoRecorrido;

import java.math.BigDecimal;
import java.time.LocalDate;

// Un día en el calendario
public record ResumenRecorridoResponse(
        LocalDate fecha,
        EstadoRecorrido estado,
        Integer sifonesEntregados,
        BigDecimal totalVendido,
        BigDecimal totalCobrado
) {
}
