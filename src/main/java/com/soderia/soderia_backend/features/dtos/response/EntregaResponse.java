package com.soderia.soderia_backend.features.dtos.response;

// Resultado de una entrega: la visita actualizada y el pago (null si no se cobró)
public record EntregaResponse(
        VisitaResponse visita,
        PagoResponse pago
) {
}
