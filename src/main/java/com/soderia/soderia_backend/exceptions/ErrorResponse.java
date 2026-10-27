package com.soderia.soderia_backend.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

// Formato común de todos los errores que devuelve la API
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        Map<String, String> errores
) {
}
