package com.soderia.soderia_backend.controllers.Delete;

import com.soderia.soderia_backend.features.services.interfaces.ClienteService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Clientes", description = "Alta, búsqueda, envases en casa y baja lógica")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'REPARTIDOR')")
@SecurityRequirement(name = "bearerAuth") //Asi Swagger sabe que los endpoints de clientes necesitan el token.
public class ClienteDeleteController {

    private final ClienteService clienteService;

    // HU-04: baja lógica. No se borra de la base, queda inactivo con todo su historial.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id) {
        clienteService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
