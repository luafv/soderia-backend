package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.DeudaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PagoGetController {

    private final PagoService pagoService;

    // Pagos de un cliente, del más nuevo al más viejo
    @GetMapping("/pagos/cliente/{idCliente}")
    public ResponseEntity<List<PagoResponse>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pagoService.listarPorCliente(idCliente));
    }

    // Deuda del cliente, en unidades y en pesos al precio de hoy
    @GetMapping("/clientes/{idCliente}/deuda")
    public ResponseEntity<DeudaResponse> deuda(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pagoService.calcularDeuda(idCliente));
    }
}
