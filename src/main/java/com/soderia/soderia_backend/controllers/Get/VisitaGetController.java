package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import com.soderia.soderia_backend.features.services.interfaces.VisitaService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Visitas y entregas", description = "Visitas del día, entregas y ausentes")
@RestController
@RequestMapping("/api/visitas")
@RequiredArgsConstructor
public class VisitaGetController {

    private final VisitaService visitaService;

    // Hoja de ruta del día
    // GET /api/visitas?fecha=2026-10-28
    // GET /api/visitas?fecha=2026-10-28&estado=PENDIENTE
    @GetMapping
    public ResponseEntity<List<VisitaResponse>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) EstadoVisita estado) {
        return ResponseEntity.ok(visitaService.listarPorFecha(fecha, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(visitaService.buscarPorId(id));
    }

    // Historial de visitas de un cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VisitaResponse>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(visitaService.listarPorCliente(idCliente));
    }
}
