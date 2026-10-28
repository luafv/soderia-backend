package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;

// Un producto fijo del cliente, con el precio actual del producto
public record ProductoClienteResponse(
        Long idDetalleProductoCliente,
        Long idProducto,
        String nombreProducto,
        BigDecimal precioActual,
        Integer cantidad
) {
}
