package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// Carga del stock del día (sifones llenos en el transporte)
public record StockRequest(

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock
) {
}
