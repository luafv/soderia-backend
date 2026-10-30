package com.soderia.soderia_backend.controllers.Patch;

import com.soderia.soderia_backend.features.dtos.request.EntregaRequest;
import com.soderia.soderia_backend.features.dtos.response.EntregaResponse;
import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.services.interfaces.VisitaService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Visitas y entregas", description = "Visitas del día, entregas y ausentes")
@RestController
@RequestMapping("/api/visitas")
@RequiredArgsConstructor
public class VisitaPatchController {

    private final VisitaService visitaService;

    // Marca la visita como ENTREGADA, descuenta stock y opcionalmente registra el cobro.
    // El body es opcional.
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<EntregaResponse> entregar(@PathVariable Long id,
                                                    @Valid @RequestBody(required = false) EntregaRequest request) {
        return ResponseEntity.ok(visitaService.entregar(id, request));
    }

    @PatchMapping("/{id}/ausente")
    public ResponseEntity<VisitaResponse> marcarAusente(@PathVariable Long id) {
        return ResponseEntity.ok(visitaService.marcarAusente(id));
    }
}
