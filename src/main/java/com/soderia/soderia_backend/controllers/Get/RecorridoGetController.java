package com.soderia.soderia_backend.controllers.Get;

import com.soderia.soderia_backend.features.dtos.response.ReporteRecorridoResponse;
import com.soderia.soderia_backend.features.dtos.response.ResumenRecorridoResponse;
import com.soderia.soderia_backend.features.services.interfaces.RecorridoService;
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
@RequestMapping("/api/recorridos")
@RequiredArgsConstructor
public class RecorridoGetController {

    private final RecorridoService recorridoService;

    // Calendario: GET /api/recorridos?desde=2026-11-01&hasta=2026-11-30
    @GetMapping
    public ResponseEntity<List<ResumenRecorridoResponse>> historial(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(recorridoService.historial(desde, hasta));
    }

    // Reporte completo del día: GET /api/recorridos/2026-11-03
    @GetMapping("/{fecha}")
    public ResponseEntity<ReporteRecorridoResponse> reporte(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(recorridoService.reporte(fecha));
    }
}
