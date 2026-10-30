package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.PagoRequest;
import com.soderia.soderia_backend.features.dtos.response.DeudaResponse;
import com.soderia.soderia_backend.features.dtos.response.EstadoCuentaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.Visita;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagoService {

    // Pago suelto (no vinculado a una entrega)
    PagoResponse registrar(PagoRequest request);

    // Usado también por la entrega de visitas. visita = null si es un pago suelto.
    PagoResponse registrarPago(Cliente cliente, Producto producto, BigDecimal monto,
                               MetodoPago metodoPago, LocalDate fecha, Visita visita);

    List<PagoResponse> listarPorCliente(Long idCliente);

    void eliminar(Long idPago);

    // HU-11: deuda total (resumen por producto)
    DeudaResponse calcularDeuda(Long idCliente);

    // HU-06 / HU-13: cada entrega con su estado de pago + resumen + total
    EstadoCuentaResponse estadoDeCuenta(Long idCliente);
}
