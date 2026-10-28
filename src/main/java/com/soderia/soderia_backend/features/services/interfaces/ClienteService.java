package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.CantidadRequest;
import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.SifonesRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteDetalleResponse;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;

import java.time.DayOfWeek;
import java.util.List;

public interface ClienteService {

    // --- Clientes ---
    List<ClienteResponse> listar(String buscar, String direccion, boolean incluirInactivos);

    ClienteResponse buscarPorId(Long id);

    ClienteDetalleResponse detalle(Long id);

    List<ClienteResponse> listarPorDia(DayOfWeek dia);

    ClienteResponse crear(ClienteRequest request);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    ClienteResponse actualizarSifones(Long id, SifonesRequest request);

    void darDeBaja(Long id);

    ClienteResponse reactivar(Long id);

    // --- Productos fijos del cliente ---
    List<ProductoClienteResponse> listarProductos(Long idCliente);

    ProductoClienteResponse agregarProducto(Long idCliente, ProductoClienteRequest request);

    ProductoClienteResponse actualizarCantidadProducto(Long idCliente, Long idProducto, CantidadRequest request);

    void quitarProducto(Long idCliente, Long idProducto);
}
