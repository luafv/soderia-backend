package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// Para actualizar solo el precio (ej: cuando aumenta el sifón)
public record PrecioRequest(

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
        BigDecimal precio
) {
}
