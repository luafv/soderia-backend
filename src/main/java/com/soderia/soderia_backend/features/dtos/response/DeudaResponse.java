package com.soderia.soderia_backend.features.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record DeudaResponse(
        Long idCliente,
        String cliente,
        List<DeudaProductoResponse> productos,
        BigDecimal totalEnPesos
) {
}
