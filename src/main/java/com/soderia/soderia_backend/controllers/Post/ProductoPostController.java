package com.soderia.soderia_backend.controllers.Post;

import com.soderia.soderia_backend.features.dtos.request.ProductoRequest;
import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;
import com.soderia.soderia_backend.features.services.interfaces.ProductoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Productos", description = "Catálogo, precio y stock del camión")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoPostController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/productos/" + creado.idProducto()))
                .body(creado);
    }
}
