package com.soderia.soderia_backend.controllers.Put;

import com.soderia.soderia_backend.features.dtos.request.PedidoRequest;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.services.interfaces.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoPutController {

    private final PedidoService pedidoService;

    // Solo se puede modificar un pedido PENDIENTE
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizar(id, request));
    }
}
