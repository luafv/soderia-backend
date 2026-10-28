package com.soderia.soderia_backend.features.dtos.response;

// Resultado de una entrega: el pedido actualizado y el pago (null si no se cobró)
public record EntregaResponse(
        PedidoResponse pedido,
        PagoResponse pago
) {
}
