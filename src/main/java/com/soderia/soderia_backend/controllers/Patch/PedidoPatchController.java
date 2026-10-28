package com.soderia.soderia_backend.controllers.Patch;

import com.soderia.soderia_backend.features.dtos.request.EntregaRequest;
import com.soderia.soderia_backend.features.dtos.response.EntregaResponse;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.services.interfaces.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoPatchController {

    private final PedidoService pedidoService;

    // Marca el pedido como ENTREGADO, descuenta stock y opcionalmente registra el cobro.
    // El body es opcional.
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<EntregaResponse> entregar(@PathVariable Long id,
                                                    @Valid @RequestBody(required = false) EntregaRequest request) {
        return ResponseEntity.ok(pedidoService.entregar(id, request));
    }

    @PatchMapping("/{id}/ausente")
    public ResponseEntity<PedidoResponse> marcarAusente(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.marcarAusente(id));
    }
}
