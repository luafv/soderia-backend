package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.ConflictoException;
import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.features.dtos.request.PrecioRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoRequest;
import com.soderia.soderia_backend.features.dtos.response.ProductoResponse;
import com.soderia.soderia_backend.features.mappers.ProductoMapper;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.services.interfaces.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return productoMapper.toResponse(obtener(id));
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ConflictoException("Ya existe un producto con el nombre '" + request.nombre().trim() + "'");
        }
        Producto guardado = productoRepository.save(productoMapper.toEntity(request));
        return productoMapper.toResponse(guardado);
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtener(id);
        if (productoRepository.existsByNombreIgnoreCaseAndIdProductoNot(request.nombre().trim(), id)) {
            throw new ConflictoException("Ya existe otro producto con el nombre '" + request.nombre().trim() + "'");
        }
        productoMapper.updateEntity(producto, request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizarPrecio(Long id, PrecioRequest request) {
        Producto producto = obtener(id);
        producto.setPrecio(request.precio());
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = obtener(id);
        productoRepository.delete(producto);
        // flush para que, si el producto está usado en pedidos o pagos, el error salte acá
        productoRepository.flush();
    }

    private Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }
}
