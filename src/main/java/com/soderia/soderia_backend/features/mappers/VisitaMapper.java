package com.soderia.soderia_backend.features.mappers;

import com.soderia.soderia_backend.features.dtos.response.DetalleVisitaResponse;
import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.models.DetalleVisita;
import com.soderia.soderia_backend.features.models.Visita;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class VisitaMapper {

    public VisitaResponse toResponse(Visita visita) {
        List<DetalleVisitaResponse> detalles = visita.getDetalles().stream()
                .map(this::toResponse)
                .toList();

        BigDecimal total = detalles.stream()
                .map(DetalleVisitaResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new VisitaResponse(
                visita.getIdVisita(),
                visita.getCliente().getIdCliente(),
                visita.getCliente().getNombre() + " " + visita.getCliente().getApellido(),
                visita.getCliente().getDireccion(),
                visita.getFecha(),
                visita.getEstado(),
                detalles,
                total,
                visita.getVaciosRetirados(),
                visita.getCliente().getSifonesEnCasa()
        );
    }

    public DetalleVisitaResponse toResponse(DetalleVisita detalle) {
        BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
        return new DetalleVisitaResponse(
                detalle.getIdDetalleVisita(),
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                subtotal
        );
    }
}
