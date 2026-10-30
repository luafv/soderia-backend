package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDate;

// Un renglón entregado con su estado de pago (en unidades y en pesos al precio de hoy)
public record EntregaCuentaResponse(
        Long idVisita,
        LocalDate fecha,
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal unidadesCubiertas,
        BigDecimal unidadesPendientes,
        BigDecimal pendienteEnPesos,
        EstadoPago estado
) {
}
