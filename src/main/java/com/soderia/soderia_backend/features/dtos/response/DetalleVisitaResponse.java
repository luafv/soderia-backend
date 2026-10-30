package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;

public record DetalleVisitaResponse(
        Long idDetalleVisita,
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
