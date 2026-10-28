package com.soderia.soderia_backend.features.dtos.request;

import java.time.LocalDate;

// Genera los pedidos de una fecha a partir de los productos fijos de cada cliente.
// La fecha es opcional: si no se manda, se usa la de hoy.
public record GenerarPedidosRequest(
        LocalDate fecha
) {
}
