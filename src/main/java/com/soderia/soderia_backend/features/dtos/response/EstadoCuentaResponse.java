package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Estado de cuenta del cliente: cada entrega con su estado de pago, el resumen por producto y el total.
// totalEnPesos negativo = saldo a favor del cliente.
public record EstadoCuentaResponse(
        Long idCliente,
        String cliente,
        String telefono,
        LocalDate fecha,
        List<EntregaCuentaResponse> entregas,
        List<DeudaProductoResponse> resumen,
        BigDecimal totalEnPesos
) {
}
