package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.PagoRequest;
import com.soderia.soderia_backend.features.dtos.response.DeudaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagoService {

    PagoResponse registrar(PagoRequest request);

    // Usado también por la entrega de pedidos cuando se cobra en el momento
    PagoResponse registrarPago(Cliente cliente, Producto producto, BigDecimal monto,
                               MetodoPago metodoPago, LocalDate fecha);

    List<PagoResponse> listarPorCliente(Long idCliente);

    void eliminar(Long idPago);

    DeudaResponse calcularDeuda(Long idCliente);
}
