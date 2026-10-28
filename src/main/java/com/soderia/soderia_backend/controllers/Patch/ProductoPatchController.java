package com.soderia.soderia_backend.controllers.Patch;

import com.soderia.soderia_backend.features.dtos.request.PrecioRequest;
import com.soderia.soderia_backend.features.dtos.request.StockRequest;
import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;
import com.soderia.soderia_backend.features.services.interfaces.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoPatchController {

    private final ProductoService productoService;

    // Actualiza solo el precio (ej: aumento del sifón)
    @PatchMapping("/{id}/precio")
    public ResponseEntity<ProductoResponse> actualizarPrecio(@PathVariable Long id,
                                                             @Valid @RequestBody PrecioRequest request) {
        return ResponseEntity.ok(productoService.actualizarPrecio(id, request));
    }

    // HU-07: carga del stock del día (sifones llenos en el transporte)
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(@PathVariable Long id,
                                                            @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(productoService.actualizarStock(id, request));
    }
}
