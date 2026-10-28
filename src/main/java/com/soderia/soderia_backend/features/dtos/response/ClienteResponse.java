package com.soderia.soderia_backend.features.dtos.response;

import java.time.DayOfWeek;

public record ClienteResponse(
        Long idCliente,
        String nombre,
        String apellido,
        String direccion,
        String email,
        String telefono,
        DayOfWeek diaEntrega,
        Integer sifonesEnCasa,
        Boolean activo
) {
}
