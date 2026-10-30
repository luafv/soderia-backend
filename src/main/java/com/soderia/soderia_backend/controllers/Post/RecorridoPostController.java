package com.soderia.soderia_backend.controllers.Post;

import com.soderia.soderia_backend.features.dtos.request.IniciarRecorridoRequest;
import com.soderia.soderia_backend.features.dtos.response.ReporteRecorridoResponse;
import com.soderia.soderia_backend.features.services.interfaces.RecorridoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Recorridos", description = "Inicio y cierre del día, reporte y calendario")
@RestController
@RequestMapping("/api/recorridos")
@RequiredArgsConstructor
public class RecorridoPostController {

    private final RecorridoService recorridoService;

    // Inicio del día: carga el camión, abre el recorrido y genera las visitas
    @PostMapping("/iniciar")
    public ResponseEntity<ReporteRecorridoResponse> iniciar(@Valid @RequestBody IniciarRecorridoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recorridoService.iniciar(request));
    }

    // Fin del día: los no visitados pasan a AUSENTE y devuelve el reporte
    @PostMapping("/{fecha}/finalizar")
    public ResponseEntity<ReporteRecorridoResponse> finalizar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(recorridoService.finalizar(fecha));
    }
}
