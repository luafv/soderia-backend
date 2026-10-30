package com.soderia.soderia_backend.controllers.Put;

import com.soderia.soderia_backend.features.dtos.request.ProductoRequest;
import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;
import com.soderia.soderia_backend.features.services.interfaces.ProductoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Productos", description = "Catálogo, precio y stock del camión")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoPutController {

    private final ProductoService productoService;

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }
}
