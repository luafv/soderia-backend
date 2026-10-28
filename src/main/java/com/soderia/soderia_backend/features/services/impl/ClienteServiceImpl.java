package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.ConflictoException;
import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.features.dtos.request.CantidadRequest;
import com.soderia.soderia_backend.features.dtos.request.ClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.ProductoClienteRequest;
import com.soderia.soderia_backend.features.dtos.request.SifonesRequest;
import com.soderia.soderia_backend.features.dtos.response.ClienteDetalleResponse;
import com.soderia.soderia_backend.features.dtos.response.ClienteResponse;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.dtos.response.ProductoClienteResponse;
import com.soderia.soderia_backend.features.mappers.ClienteMapper;
import com.soderia.soderia_backend.features.mappers.DetalleProductoClienteMapper;
import com.soderia.soderia_backend.features.mappers.PedidoMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.DetalleProductoCliente;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.DetalleProductoClienteRepository;
import com.soderia.soderia_backend.features.repositories.PedidoRepository;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.services.interfaces.ClienteService;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import lombok.RequiredArgsConstructor;
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
    private final PedidoRepository pedidoRepository;
    private final PagoService pagoService;
    private final ClienteMapper clienteMapper;
    private final DetalleProductoClienteMapper detalleProductoClienteMapper;
    private final PedidoMapper pedidoMapper;

    // ===================== Clientes =====================

    // HU-05: lista con filtros opcionales por nombre/apellido y por dirección
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String buscar, String direccion, boolean incluirInactivos) {
        return clienteRepository.buscar(texto(buscar), texto(direccion), incluirInactivos).stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return clienteMapper.toResponse(obtenerCliente(id));
    }

    // HU-06: datos, productos fijos, pedidos por entregar y estado de cuenta
    @Override
    @Transactional(readOnly = true)
    public ClienteDetalleResponse detalle(Long id) {
        Cliente cliente = obtenerCliente(id);
        List<ProductoClienteResponse> fijos = detalleProductoClienteRepository.findByClienteIdCliente(id).stream()
                .map(detalleProductoClienteMapper::toResponse)
                .toList();
        List<PedidoResponse> pendientes = pedidoRepository
                .findByClienteIdClienteAndEstadoOrderByFechaEntregaAsc(id, EstadoPedido.PENDIENTE).stream()
                .map(pedidoMapper::toResponse)
                .toList();
        return new ClienteDetalleResponse(
                clienteMapper.toResponse(cliente),
                fijos,
                pendientes,
                pagoService.estadoDeCuenta(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarPorDia(DayOfWeek dia) {
        return clienteRepository.findByDiaEntregaAndActivoTrueOrderByDireccionAsc(dia).stream()
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

    // Corrección manual de los envases que tiene el cliente
    @Override
    public ClienteResponse actualizarSifones(Long id, SifonesRequest request) {
        Cliente cliente = obtenerCliente(id);
        cliente.setSifonesEnCasa(request.sifonesEnCasa());
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    // HU-04: baja lógica. El cliente queda en la base con todo su historial.
    @Override
    public void darDeBaja(Long id) {
        Cliente cliente = obtenerCliente(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Override
    public ClienteResponse reactivar(Long id) {
        Cliente cliente = obtenerCliente(id);
        cliente.setActivo(true);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
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
        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + request.idProducto()));

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

    // null o vacío = sin filtro
    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }

    private DetalleProductoCliente obtenerDetalle(Long idCliente, Long idProducto) {
        obtenerCliente(idCliente);
        return detalleProductoClienteRepository.findByClienteIdClienteAndProductoIdProducto(idCliente, idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El cliente " + idCliente + " no tiene asignado el producto " + idProducto));
    }
}
