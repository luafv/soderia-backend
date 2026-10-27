package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Para asignarle a un cliente un producto fijo (ej: 3 sifones por entrega)
public record ProductoClienteRequest(

        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad
) {
}
