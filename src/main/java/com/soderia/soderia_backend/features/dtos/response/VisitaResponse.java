package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoVisita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record VisitaResponse(
        Long idVisita,
        Long idCliente,
        String cliente,
        String direccion,
        LocalDate fecha,
        EstadoVisita estado,
        List<DetalleVisitaResponse> detalles,
        BigDecimal total,
        Integer vaciosRetirados,
        // Envases que el cliente tiene en casa: cantidad sugerida para dejarle (cambio completo)
        Integer sugerido
) {
}
