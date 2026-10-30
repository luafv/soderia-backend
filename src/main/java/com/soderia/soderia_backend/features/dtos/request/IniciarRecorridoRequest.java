package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

// Inicio del día: con cuántos sifones sale el camión.
// La fecha es opcional: si no se manda, es hoy.
public record IniciarRecorridoRequest(

        LocalDate fecha,

        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "El stock con el que sale el camión es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock
) {
}
