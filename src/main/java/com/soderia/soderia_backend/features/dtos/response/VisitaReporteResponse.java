package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoVisita;

import java.math.BigDecimal;

// Una visita del día dentro del reporte
public record VisitaReporteResponse(
        Long idVisita,
        Long idCliente,
        String cliente,
        String direccion,
        EstadoVisita estado,
        Integer sifonesDejados,
        Integer vaciosRetirados,
        BigDecimal importe,
        BigDecimal cobrado
) {
}
