package com.soderia.soderia_backend.controllers.Post;

import com.soderia.soderia_backend.features.dtos.request.GenerarPedidosRequest;
import com.soderia.soderia_backend.features.dtos.request.PedidoRequest;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.services.interfaces.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoPostController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = pedidoService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + creado.idPedido()))
                .body(creado);
    }

    // Crea los pedidos PENDIENTES de una fecha con los productos fijos de cada cliente de ese día
    @PostMapping("/generar")
    public ResponseEntity<List<PedidoResponse>> generar(@Valid @RequestBody GenerarPedidosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.generarPedidosDelDia(request));
    }
}
