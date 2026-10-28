package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;

public record DetallePedidoResponse(
        Long idDetallePedido,
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
