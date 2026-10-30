package com.soderia.soderia_backend.controllers.Delete;

import com.soderia.soderia_backend.features.services.interfaces.VisitaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Visitas y entregas", description = "Visitas del día, entregas y ausentes")
@RestController
@RequestMapping("/api/visitas")
@RequiredArgsConstructor
public class VisitaDeleteController {

    private final VisitaService visitaService;

    // Solo se puede borrar una visita PENDIENTE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        visitaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
