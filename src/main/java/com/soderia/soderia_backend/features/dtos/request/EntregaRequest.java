package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.Valid;

import java.util.List;

// Todo es opcional:
// - detalles: si se mandan, reemplazan lo que tenía el pedido (lo que realmente se entregó)
// - pago: si se manda, se registra el cobro en el momento
public record EntregaRequest(
        List<@Valid DetallePedidoRequest> detalles,
        @Valid PagoEntregaRequest pago
) {
}
