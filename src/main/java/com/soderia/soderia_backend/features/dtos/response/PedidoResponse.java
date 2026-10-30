package com.soderia.soderia_backend.features.dtos.response;

import com.soderia.soderia_backend.features.models.enums.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(
        Long idPedido,
        Long idCliente,
        String cliente,
        String direccion,
        LocalDate fechaEntrega,
        EstadoPedido estado,
        List<DetallePedidoResponse> detalles,
        BigDecimal total,
        Integer vaciosRetirados,
        // Envases que el cliente tiene en casa: cantidad sugerida para dejarle (cambio completo)
        Integer sugerido
) {
}
