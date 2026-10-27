package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;

// Datos que devuelve la API sobre un producto
public record ProductoResponse(
        Long idProducto,
        String nombre,
        BigDecimal precio,
        Integer stock
) {
}
