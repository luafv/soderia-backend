package com.soderia.soderia_backend.features.dtos.request;

public record LoginRequest(
    String usuario,
    String password
) {}
