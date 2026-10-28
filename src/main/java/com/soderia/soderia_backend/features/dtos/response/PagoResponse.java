package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoResponse(
        Long idPago,
        Long idCliente,
        String cliente,
        Long idProducto,
        String nombreProducto,
        LocalDate fechaPago,
        BigDecimal monto,
        BigDecimal cantidad,
        MetodoPago metodoPago
) {
}
