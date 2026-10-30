package com.soderia.soderia_backend.features.dtos.response;

import java.util.List;

// Todo lo del cliente en una sola consulta (HU-06): datos (incluye sifones en casa),
// visitas pendientes y estado de cuenta
public record ClienteDetalleResponse(
        ClienteResponse cliente,
        List<PedidoResponse> pedidosPendientes,
        EstadoCuentaResponse estadoCuenta
) {
}
