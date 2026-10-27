package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.CantidadRequest;
import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoClienteRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;

import java.time.DayOfWeek;
import java.util.List;

public interface ClienteService {

    // --- Clientes ---
    List<ClienteResponse> listar(String buscar);

    ClienteResponse buscarPorId(Long id);

    List<ClienteResponse> listarPorDia(DayOfWeek dia);

    ClienteResponse crear(ClienteRequest request);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    void eliminar(Long id);

    // --- Productos fijos del cliente ---
    List<ProductoClienteResponse> listarProductos(Long idCliente);

    ProductoClienteResponse agregarProducto(Long idCliente, ProductoClienteRequest request);

    ProductoClienteResponse actualizarCantidadProducto(Long idCliente, Long idProducto, CantidadRequest request);

    void quitarProducto(Long idCliente, Long idProducto);
}
