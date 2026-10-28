package com.soderia.soderia_backend.features.dtos.request;

import com.soderia.soderia_backend.features.models.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

// Pago suelto (ej: el cliente pasa a saldar deuda)
public record PagoRequest(

        @NotNull(message = "El cliente es obligatorio")
        Long idCliente,

        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
        @Digits(integer = 10, fraction = 2, message = "El monto admite hasta 10 enteros y 2 decimales")
        BigDecimal monto,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        // Opcional: si no se manda, se usa la fecha de hoy
        LocalDate fechaPago
) {
}
