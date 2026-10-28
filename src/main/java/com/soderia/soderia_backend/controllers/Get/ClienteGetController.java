package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.ClienteDetalleResponse;
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

    // HU-05. Todos los filtros son opcionales:
    // GET /api/clientes
    // GET /api/clientes?buscar=gom
    // GET /api/clientes?direccion=san martin
    // GET /api/clientes?incluirInactivos=true   (incluye los dados de baja)
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String direccion,
            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(clienteService.listar(buscar, direccion, incluirInactivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // HU-06: datos, sifones en casa, productos fijos, pedidos por entregar y estado de cuenta
    @GetMapping("/{id}/detalle")
    public ResponseEntity<ClienteDetalleResponse> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.detalle(id));
    }

    // GET /api/clientes/dia/TUESDAY -> recorrido del martes (solo clientes activos)
    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<ClienteResponse>> listarPorDia(@PathVariable DayOfWeek dia) {
        return ResponseEntity.ok(clienteService.listarPorDia(dia));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoClienteResponse>> listarProductos(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.listarProductos(id));
    }
}
