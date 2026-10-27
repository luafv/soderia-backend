package com.soderia.soderia_backend.features.mappers;

import com.soderia.soderia_backend.features.dtos.request.ProductoRequest;
import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;
import com.soderia.soderia_backend.features.models.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {
        return Producto.builder()
                .nombre(request.nombre().trim())
                .precio(request.precio())
                .stock(request.stock())
                .build();
    }

    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
    }

    // Copia los datos del request sobre una entidad que ya existe (para el PUT)
    public void updateEntity(Producto producto, ProductoRequest request) {
        producto.setNombre(request.nombre().trim());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
    }
}
