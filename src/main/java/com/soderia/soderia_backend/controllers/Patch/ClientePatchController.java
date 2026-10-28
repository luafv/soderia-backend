package com.soderia.soderia_backend.controllers.Patch;

import com.soderia.soderia_backend.features.dtos.request.SifonesRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClientePatchController {

    private final ClienteService clienteService;

    // Corrección manual de los envases que tiene el cliente en su casa
    @PatchMapping("/{id}/sifones")
    public ResponseEntity<ClienteResponse> actualizarSifones(@PathVariable Long id,
                                                             @Valid @RequestBody SifonesRequest request) {
        return ResponseEntity.ok(clienteService.actualizarSifones(id, request));
    }

    // Vuelve a dar de alta un cliente dado de baja
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ClienteResponse> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.reactivar(id));
    }
}
