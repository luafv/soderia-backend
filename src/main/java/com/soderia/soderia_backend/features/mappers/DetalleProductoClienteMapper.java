package com.soderia.soderia_backend.features.mappers;

import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;
import com.soderia.soderia_backend.features.models.DetalleProductoCliente;
import org.springframework.stereotype.Component;

@Component
public class DetalleProductoClienteMapper {

    public ProductoClienteResponse toResponse(DetalleProductoCliente detalle) {
        return new ProductoClienteResponse(
                detalle.getIdDetalleProductoCliente(),
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getProducto().getPrecio(),
                detalle.getCantidad()
        );
    }
}
