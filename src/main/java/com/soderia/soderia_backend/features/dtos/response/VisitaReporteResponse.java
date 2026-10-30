package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoPedido;

import java.math.BigDecimal;

// Una visita del día dentro del reporte
public record VisitaReporteResponse(
        Long idPedido,
        Long idCliente,
        String cliente,
        String direccion,
        EstadoPedido estado,
        Integer sifonesDejados,
        Integer vaciosRetirados,
        BigDecimal importe,
        BigDecimal cobrado
) {
}
