package com.soderia.soderia_backend.features.mappers;

import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.models.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getIdPago(),
                pago.getCliente().getIdCliente(),
                pago.getCliente().getNombre() + " " + pago.getCliente().getApellido(),
                pago.getProducto().getIdProducto(),
                pago.getProducto().getNombre(),
                pago.getFechaPago(),
                pago.getMonto(),
                pago.getCantidad(),
                pago.getMetodoPago()
        );
    }
}
