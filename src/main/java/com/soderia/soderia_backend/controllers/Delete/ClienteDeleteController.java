package com.soderia.soderia_backend.controllers.Delete;

import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteDeleteController {

    private final ClienteService clienteService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Quita un producto fijo del cliente
    @DeleteMapping("/{id}/productos/{idProducto}")
    public ResponseEntity<Void> quitarProducto(@PathVariable Long id, @PathVariable Long idProducto) {
        clienteService.quitarProducto(id, idProducto);
        return ResponseEntity.noContent().build();
    }
}
