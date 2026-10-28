package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.EntregaRequest;
import com.soderia.soderia_backend.features.dtos.request.GenerarPedidosRequest;
import com.soderia.soderia_backend.features.dtos.request.PedidoRequest;
import com.soderia.soderia_backend.features.dtos.response.EntregaResponse;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;

import java.time.LocalDate;
import java.util.List;

public interface PedidoService {

    List<PedidoResponse> listarPorFecha(LocalDate fecha, EstadoPedido estado);

    List<PedidoResponse> listarPorCliente(Long idCliente);

    PedidoResponse buscarPorId(Long id);

    PedidoResponse crear(PedidoRequest request);

    List<PedidoResponse> generarPedidosDelDia(GenerarPedidosRequest request);

    PedidoResponse actualizar(Long id, PedidoRequest request);

    EntregaResponse entregar(Long id, EntregaRequest request);

    PedidoResponse marcarAusente(Long id);

    void eliminar(Long id);
}
