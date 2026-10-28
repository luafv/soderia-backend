package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

// Todo es opcional:
// - detalles: si se mandan, reemplazan lo que tenía el pedido (lo que realmente se entregó)
// - vaciosRetirados: envases vacíos que se llevó el repartidor. Si no se manda, se asume
//   el cambio 1 a 1 (se lleva tantos vacíos como llenos deja)
// - pago: si se manda, se registra el cobro en el momento y queda vinculado a esta entrega
public record EntregaRequest(
        List<@Valid DetallePedidoRequest> detalles,

        @PositiveOrZero(message = "Los vacíos retirados no pueden ser negativos")
        Integer vaciosRetirados,

        @Valid PagoEntregaRequest pago
) {
}
