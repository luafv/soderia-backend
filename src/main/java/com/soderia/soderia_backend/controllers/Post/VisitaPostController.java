package com.soderia.soderia_backend.controllers.Post;

import com.soderia.soderia_backend.features.dtos.request.GenerarVisitasRequest;
import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.services.interfaces.VisitaService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Tag(name = "Visitas y entregas", description = "Visitas del día, entregas y ausentes")
@RestController
@RequestMapping("/api/visitas")
@RequiredArgsConstructor
public class VisitaPostController {

    private final VisitaService visitaService;

    // HU-08: crea las visitas PENDIENTES de una fecha para cada cliente activo de ese día (sin cantidades).
    // El body es opcional: sin body (o sin fecha) genera las de hoy.
    @PostMapping("/generar")
    public ResponseEntity<List<VisitaResponse>> generar(@RequestBody(required = false) GenerarVisitasRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaService.generarVisitasDelDia(request));
    }
}
