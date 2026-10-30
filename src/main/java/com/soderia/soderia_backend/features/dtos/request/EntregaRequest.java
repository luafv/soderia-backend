package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

// - detalles: lo que el repartidor dejó en la casa. Obligatorio si la visita no tiene cantidades
//   (las visitas generadas del día no tienen); si se manda, reemplaza lo que tuviera el pedido.
// - vaciosRetirados: envases vacíos que se llevó. Si no se manda, se asume el cambio:
//   tantos vacíos como llenos deja, sin superar los envases que tenía el cliente.
// - pago: si se manda, se registra el cobro en el momento y queda vinculado a esta entrega
public record EntregaRequest(
        List<@Valid DetallePedidoRequest> detalles,

        @PositiveOrZero(message = "Los vacíos retirados no pueden ser negativos")
        Integer vaciosRetirados,

        @Valid PagoEntregaRequest pago
) {
}
