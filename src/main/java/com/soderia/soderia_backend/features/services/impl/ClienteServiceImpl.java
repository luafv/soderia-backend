package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.ConflictoException;
import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.features.dtos.request.CantidadRequest;
import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoClienteRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;
import com.soderia.soderia_backend.features.mappers.ClienteMapper;
import com.soderia.soderia_backend.features.mappers.DetalleProductoClienteMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.DetalleProductoCliente;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.DetalleProductoClienteRepository;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetalleProductoClienteRepository detalleProductoClienteRepository;
    private final ClienteMapper clienteMapper;
    private final DetalleProductoClienteMapper detalleProductoClienteMapper;

    // ===================== Clientes =====================

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String buscar) {
        List<Cliente> clientes = (buscar == null || buscar.isBlank())
                ? clienteRepository.findAll(Sort.by("apellido", "nombre"))
                : clienteRepository
                    .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrderByApellidoAscNombreAsc(
                            buscar.trim(), buscar.trim());
        return clientes.stream().map(clienteMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return clienteMapper.toResponse(obtenerCliente(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarPorDia(DayOfWeek dia) {
        return clienteRepository.findByDiaEntregaOrderByDireccionAsc(dia).stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        Cliente guardado = clienteRepository.save(clienteMapper.toEntity(request));
        return clienteMapper.toResponse(guardado);
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = obtenerCliente(id);
        clienteMapper.updateEntity(cliente, request);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public void eliminar(Long id) {
        Cliente cliente = obtenerCliente(id);
        // Los productos fijos se borran junto con el cliente
        detalleProductoClienteRepository.deleteByClienteIdCliente(id);
        clienteRepository.delete(cliente);
        // Si el cliente tiene pedidos o pagos, la base no deja borrarlo -> 409
        clienteRepository.flush();
    }

    // ============ Productos fijos del cliente ============

    @Override
    @Transactional(readOnly = true)
    public List<ProductoClienteResponse> listarProductos(Long idCliente) {
        obtenerCliente(idCliente);
        return detalleProductoClienteRepository.findByClienteIdCliente(idCliente).stream()
                .map(detalleProductoClienteMapper::toResponse)
                .toList();
    }

    @Override
    public ProductoClienteResponse agregarProducto(Long idCliente, ProductoClienteRequest request) {
        Cliente cliente = obtenerCliente(idCliente);
        Producto producto = obtenerProducto(request.idProducto());

        if (detalleProductoClienteRepository.existsByClienteIdClienteAndProductoIdProducto(idCliente, request.idProducto())) {
            throw new ConflictoException("El cliente ya tiene asignado el producto '" + producto.getNombre()
                    + "'. Modificá la cantidad en lugar de agregarlo de nuevo");
        }

        DetalleProductoCliente detalle = DetalleProductoCliente.builder()
                .cliente(cliente)
                .producto(producto)
                .cantidad(request.cantidad())
                .build();
        return detalleProductoClienteMapper.toResponse(detalleProductoClienteRepository.save(detalle));
    }

    @Override
    public ProductoClienteResponse actualizarCantidadProducto(Long idCliente, Long idProducto, CantidadRequest request) {
        DetalleProductoCliente detalle = obtenerDetalle(idCliente, idProducto);
        detalle.setCantidad(request.cantidad());
        return detalleProductoClienteMapper.toResponse(detalleProductoClienteRepository.save(detalle));
    }

    @Override
    public void quitarProducto(Long idCliente, Long idProducto) {
        detalleProductoClienteRepository.delete(obtenerDetalle(idCliente, idProducto));
    }

    // ===================== Auxiliares =====================

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }

    private DetalleProductoCliente obtenerDetalle(Long idCliente, Long idProducto) {
        obtenerCliente(idCliente);
        return detalleProductoClienteRepository.findByClienteIdClienteAndProductoIdProducto(idCliente, idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El cliente " + idCliente + " no tiene asignado el producto " + idProducto));
    }
}
