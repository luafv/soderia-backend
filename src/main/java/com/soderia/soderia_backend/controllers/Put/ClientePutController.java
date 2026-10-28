package com.soderia.soderia_backend.controllers.Put;

import com.soderia.soderia_backend.features.dtos.request.CantidadRequest;
import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClientePutController {

    private final ClienteService clienteService;

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }

    // Cambia la cantidad de un producto fijo del cliente
    @PutMapping("/{id}/productos/{idProducto}")
    public ResponseEntity<ProductoClienteResponse> actualizarCantidadProducto(@PathVariable Long id,
                                                                              @PathVariable Long idProducto,
                                                                              @Valid @RequestBody CantidadRequest request) {
        return ResponseEntity.ok(clienteService.actualizarCantidadProducto(id, idProducto, request));
    }
}
