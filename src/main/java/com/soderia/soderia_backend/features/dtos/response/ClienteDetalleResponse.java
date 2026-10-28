package com.soderia.soderia_backend.features.dtos.response;

import java.util.List;

// Todo lo del cliente en una sola consulta (HU-06)
public record ClienteDetalleResponse(
        ClienteResponse cliente,
        List<ProductoClienteResponse> productosFijos,
        List<PedidoResponse> pedidosPendientes,
        EstadoCuentaResponse estadoCuenta
) {
}
