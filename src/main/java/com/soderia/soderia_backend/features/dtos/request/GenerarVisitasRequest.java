package com.soderia.soderia_backend.features.dtos.request;

import java.time.LocalDate;

// Genera las visitas pendientes de una fecha para los clientes activos de ese día.
// La fecha es opcional: si no se manda, se usa la de hoy.
public record GenerarVisitasRequest(
        LocalDate fecha
) {
}
