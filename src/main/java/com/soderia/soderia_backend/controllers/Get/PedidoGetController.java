package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import com.soderia.soderia_backend.features.services.interfaces.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoGetController {

    private final PedidoService pedidoService;

    // Hoja de ruta del día
    // GET /api/pedidos?fecha=2026-10-28
    // GET /api/pedidos?fecha=2026-10-28&estado=PENDIENTE
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.listarPorFecha(fecha, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    // Historial de pedidos de un cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoResponse>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(idCliente));
    }
}
