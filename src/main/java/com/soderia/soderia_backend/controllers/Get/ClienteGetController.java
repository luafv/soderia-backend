package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteGetController {

    private final ClienteService clienteService;

    // GET /api/clientes            -> todos
    // GET /api/clientes?buscar=gom -> filtra por nombre o apellido
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar(@RequestParam(required = false) String buscar) {
        return ResponseEntity.ok(clienteService.listar(buscar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // GET /api/clientes/dia/TUESDAY -> recorrido del martes
    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<ClienteResponse>> listarPorDia(@PathVariable DayOfWeek dia) {
        return ResponseEntity.ok(clienteService.listarPorDia(dia));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoClienteResponse>> listarProductos(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.listarProductos(id));
    }
}
