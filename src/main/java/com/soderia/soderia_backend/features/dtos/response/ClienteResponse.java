package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.DiaSemana;

public record ClienteResponse(
        Long idCliente,
        String nombre,
        String apellido,
        String direccion,
        String email,
        String telefono,
        DiaSemana diaEntrega,
        Integer sifonesEnCasa,
        Boolean activo
) {
}
