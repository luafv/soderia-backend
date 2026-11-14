package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;
import com.soderia.soderia_backend.features.services.interfaces.ProductoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Productos", description = "Catálogo, precio y stock del camión")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'REPARTIDOR')")
@SecurityRequirement(name = "bearerAuth") //Asi Swagger sabe que los endpoints de clientes necesitan el token.
public class ProductoGetController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }
}
