package com.soderia.soderia_backend.features.services.interfaces;

import com.soderia.soderia_backend.features.dtos.request.PrecioRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoRequest;
import com.soderia.soderia_backend.features.dtos.request.StockRequest;
import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;

import java.util.List;

public interface ProductoService {

    List<ProductoResponse> listar();

    ProductoResponse buscarPorId(Long id);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    ProductoResponse actualizarPrecio(Long id, PrecioRequest request);

    ProductoResponse actualizarStock(Long id, StockRequest request);

    void eliminar(Long id);
}
