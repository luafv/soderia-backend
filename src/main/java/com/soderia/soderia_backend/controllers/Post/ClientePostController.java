package com.soderia.soderia_backend.controllers.Post;

import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoClienteRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClientePostController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse creado = clienteService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/clientes/" + creado.idCliente()))
                .body(creado);
    }

    // Asigna un producto fijo al cliente
    @PostMapping("/{id}/productos")
    public ResponseEntity<ProductoClienteResponse> agregarProducto(@PathVariable Long id,
                                                                   @Valid @RequestBody ProductoClienteRequest request) {
        ProductoClienteResponse creado = clienteService.agregarProducto(id, request);
        return ResponseEntity
                .created(URI.create("/api/clientes/" + id + "/productos/" + creado.idProducto()))
                .body(creado);
    }
}
