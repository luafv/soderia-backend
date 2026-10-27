package com.soderia.soderia_backend.features.mappers;

import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.models.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request) {
        return Cliente.builder()
                .nombre(request.nombre().trim())
                .apellido(request.apellido().trim())
                .direccion(request.direccion().trim())
                .email(limpiar(request.email()))
                .diaEntrega(request.diaEntrega())
                .build();
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDireccion(),
                cliente.getEmail(),
                cliente.getDiaEntrega()
        );
    }

    public void updateEntity(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setDireccion(request.direccion().trim());
        cliente.setEmail(limpiar(request.email()));
        cliente.setDiaEntrega(request.diaEntrega());
    }

    // Un email vacío se guarda como null
    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
