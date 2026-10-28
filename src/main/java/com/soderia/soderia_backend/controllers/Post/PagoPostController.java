package com.soderia.soderia_backend.controllers.Post;

import com.soderia.soderia_backend.features.dtos.request.PagoRequest;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoPostController {

    private final PagoService pagoService;

    // Pago suelto (ej: el cliente salda deuda sin que haya entrega)
    @PostMapping
    public ResponseEntity<PagoResponse> registrar(@Valid @RequestBody PagoRequest request) {
        PagoResponse creado = pagoService.registrar(request);
        return ResponseEntity
                .created(URI.create("/api/pagos/" + creado.idPago()))
                .body(creado);
    }
}
