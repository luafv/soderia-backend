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
                .telefono(limpiar(request.telefono()))
                .diaEntrega(request.diaEntrega())
                .sifonesEnCasa(request.sifonesEnCasa() != null ? request.sifonesEnCasa() : 0)
                .activo(true)
                .build();
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDireccion(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDiaEntrega(),
                cliente.getSifonesEnCasa(),
                cliente.getActivo()
        );
    }

    public void updateEntity(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setDireccion(request.direccion().trim());
        cliente.setEmail(limpiar(request.email()));
        cliente.setTelefono(limpiar(request.telefono()));
        cliente.setDiaEntrega(request.diaEntrega());
        // Los sifones en casa solo cambian si se mandan
        if (request.sifonesEnCasa() != null) {
            cliente.setSifonesEnCasa(request.sifonesEnCasa());
        }
    }

    // Un texto vacío se guarda como null
    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
