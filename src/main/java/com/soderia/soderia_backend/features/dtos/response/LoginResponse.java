package com.soderia.soderia_backend.features.dtos.response;

public record LoginResponse(
    String token,
    String tipo
) {}
