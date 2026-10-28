package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;

// Cuenta de un producto: lo entregado, lo pagado y lo que se debe al precio de hoy.
// Si unidadesAdeudadas es negativo, el cliente tiene saldo a favor.
public record DeudaProductoResponse(
        Long idProducto,
        String nombreProducto,
        Long unidadesEntregadas,
        BigDecimal unidadesPagadas,
        BigDecimal unidadesAdeudadas,
        BigDecimal precioActual,
        BigDecimal deudaEnPesos
) {
}
