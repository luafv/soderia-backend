package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

// Para crear o modificar un pedido a mano
public record PedidoRequest(

        @NotNull(message = "El cliente es obligatorio")
        Long idCliente,

        @NotNull(message = "La fecha de entrega es obligatoria")
        LocalDate fechaEntrega,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<@Valid DetallePedidoRequest> detalles
) {
}
