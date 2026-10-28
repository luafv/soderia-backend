package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Genera los pedidos de una fecha a partir de los productos fijos de cada cliente
public record GenerarPedidosRequest(

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha
) {
}
