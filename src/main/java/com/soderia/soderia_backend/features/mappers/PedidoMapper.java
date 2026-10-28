package com.soderia.soderia_backend.features.mappers;

import com.soderia.soderia_backend.features.dtos.response.DetallePedidoResponse;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.models.DetallePedido;
import com.soderia.soderia_backend.features.models.Pedido;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        List<DetallePedidoResponse> detalles = pedido.getDetalles().stream()
                .map(this::toResponse)
                .toList();

        BigDecimal total = detalles.stream()
                .map(DetallePedidoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getCliente().getIdCliente(),
                pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido(),
                pedido.getCliente().getDireccion(),
                pedido.getFechaEntrega(),
                pedido.getEstado(),
                detalles,
                total
        );
    }

    public DetallePedidoResponse toResponse(DetallePedido detalle) {
        BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
        return new DetallePedidoResponse(
                detalle.getIdDetallePedido(),
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                subtotal
        );
    }
}
