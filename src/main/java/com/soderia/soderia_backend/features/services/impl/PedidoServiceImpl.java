package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.ConflictoException;
import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.exceptions.SolicitudInvalidaException;
import com.soderia.soderia_backend.features.dtos.request.DetallePedidoRequest;
import com.soderia.soderia_backend.features.dtos.request.EntregaRequest;
import com.soderia.soderia_backend.features.dtos.request.GenerarPedidosRequest;
import com.soderia.soderia_backend.features.dtos.request.PagoEntregaRequest;
import com.soderia.soderia_backend.features.dtos.request.PedidoRequest;
import com.soderia.soderia_backend.features.dtos.response.EntregaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.dtos.response.PedidoResponse;
import com.soderia.soderia_backend.features.mappers.PedidoMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.DetallePedido;
import com.soderia.soderia_backend.features.models.Pedido;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.PedidoRepository;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import com.soderia.soderia_backend.features.services.interfaces.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PagoService pagoService;
    private final PedidoMapper pedidoMapper;

    // ===================== Consultas =====================

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorFecha(LocalDate fecha, EstadoPedido estado) {
        List<Pedido> pedidos = (estado == null)
                ? pedidoRepository.findByFechaEntregaOrderByClienteDireccionAsc(fecha)
                : pedidoRepository.findByFechaEntregaAndEstadoOrderByClienteDireccionAsc(fecha, estado);
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long idCliente) {
        obtenerCliente(idCliente);
        return pedidoRepository.findByClienteIdClienteOrderByFechaEntregaDesc(idCliente).stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return pedidoMapper.toResponse(obtenerPedido(id));
    }

    // ===================== Alta y modificación =====================

    @Override
    public PedidoResponse crear(PedidoRequest request) {
        Cliente cliente = obtenerClienteActivo(request.idCliente());
        if (pedidoRepository.existsByClienteIdClienteAndFechaEntrega(cliente.getIdCliente(), request.fechaEntrega())) {
            throw new ConflictoException("El cliente ya tiene un pedido para el " + request.fechaEntrega()
                    + ". Modificá ese pedido en lugar de crear otro");
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .fechaEntrega(request.fechaEntrega())
                .build();
        cargarDetalles(pedido, request.detalles());
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public List<PedidoResponse> generarPedidosDelDia(GenerarPedidosRequest request) {
        // HU-08: si no se manda fecha, se generan los de hoy
        LocalDate fecha = (request == null || request.fecha() == null) ? LocalDate.now() : request.fecha();
        List<PedidoResponse> creados = new ArrayList<>();

        for (Cliente cliente : clienteRepository.findByDiaEntregaAndActivoTrueOrderByDireccionAsc(fecha.getDayOfWeek())) {
            // Si ya tiene pedido ese día, no se duplica
            if (pedidoRepository.existsByClienteIdClienteAndFechaEntrega(cliente.getIdCliente(), fecha)) {
                continue;
            }
            // Visita pendiente SIN cantidades: lo que se deja se registra recién en la casa del cliente.
            // La respuesta trae un "sugerido" = los envases que el cliente tiene en casa.
            Pedido pedido = Pedido.builder()
                    .cliente(cliente)
                    .fechaEntrega(fecha)
                    .build();
            creados.add(pedidoMapper.toResponse(pedidoRepository.save(pedido)));
        }
        return creados;
    }

    @Override
    public PedidoResponse actualizar(Long id, PedidoRequest request) {
        Pedido pedido = obtenerPedido(id);
        validarPendiente(pedido, "modificar");

        Cliente cliente = obtenerClienteActivo(request.idCliente());
        if (pedidoRepository.existsByClienteIdClienteAndFechaEntregaAndIdPedidoNot(
                cliente.getIdCliente(), request.fechaEntrega(), id)) {
            throw new ConflictoException("El cliente ya tiene otro pedido para el " + request.fechaEntrega());
        }

        pedido.setCliente(cliente);
        pedido.setFechaEntrega(request.fechaEntrega());
        cargarDetalles(pedido, request.detalles());
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    // ===================== Entrega =====================

    @Override
    public EntregaResponse entregar(Long id, EntregaRequest request) {
        Pedido pedido = obtenerPedido(id);
        validarPendiente(pedido, "entregar");

        // 1. Lo que el repartidor dejó en la casa. Si lo informa, reemplaza lo que tuviera el pedido.
        if (request != null && request.detalles() != null && !request.detalles().isEmpty()) {
            cargarDetalles(pedido, request.detalles());
        }
        // Una visita generada del día no tiene cantidades: hay que decir cuánto se dejó
        if (pedido.getDetalles().isEmpty()) {
            throw new SolicitudInvalidaException("Indicá cuántos sifones le dejaste al cliente (detalles). "
                    + "Sugerido: " + pedido.getCliente().getSifonesEnCasa()
                    + ". Si no estaba, marcá la visita como ausente");
        }

        // 2. Verificar stock de todo antes de descontar nada
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new ConflictoException("Stock insuficiente de '" + producto.getNombre() + "': hay "
                        + producto.getStock() + " y se quieren entregar " + detalle.getCantidad());
            }
        }

        // 3. HU-09: envases en casa del cliente = antes + llenos que deja - vacíos que se lleva.
        //    Si no se informan los vacíos, se asume el cambio: se lleva tantos vacíos como llenos deja,
        //    pero nunca más de los envases que tenía el cliente.
        Cliente cliente = pedido.getCliente();
        int llenos = pedido.getDetalles().stream().mapToInt(DetallePedido::getCantidad).sum();
        int vacios = (request != null && request.vaciosRetirados() != null)
                ? request.vaciosRetirados()
                : Math.min(llenos, cliente.getSifonesEnCasa());
        int enCasa = cliente.getSifonesEnCasa() + llenos - vacios;
        if (enCasa < 0) {
            throw new SolicitudInvalidaException("El cliente tenía " + cliente.getSifonesEnCasa()
                    + " sifones en casa y recibe " + llenos + ": no se le pueden retirar " + vacios + " vacíos");
        }
        cliente.setSifonesEnCasa(enCasa);
        pedido.setVaciosRetirados(vacios);

        // 4. Descontar stock y fijar el precio del día de la entrega
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        PedidoResponse pedidoResponse = pedidoMapper.toResponse(pedidoRepository.save(pedido));

        // 5. Si cobró en el momento, se registra el pago vinculado a esta entrega
        PagoResponse pagoResponse = null;
        if (request != null && request.pago() != null) {
            PagoEntregaRequest pago = request.pago();
            Producto productoPagado = elegirProductoDelPago(pedido, pago.idProducto());
            pagoResponse = pagoService.registrarPago(pedido.getCliente(), productoPagado,
                    pago.monto(), pago.metodoPago(), pedido.getFechaEntrega(), pedido);
        }

        return new EntregaResponse(pedidoResponse, pagoResponse);
    }

    @Override
    public PedidoResponse marcarAusente(Long id) {
        Pedido pedido = obtenerPedido(id);
        validarPendiente(pedido, "marcar como ausente");
        pedido.setEstado(EstadoPedido.AUSENTE);
        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public void eliminar(Long id) {
        Pedido pedido = obtenerPedido(id);
        validarPendiente(pedido, "eliminar");
        pedidoRepository.delete(pedido);
    }

    // ===================== Auxiliares =====================

    // Reemplaza los renglones del pedido por los del request
    private void cargarDetalles(Pedido pedido, List<DetallePedidoRequest> detalles) {
        Set<Long> productosVistos = new HashSet<>();
        for (DetallePedidoRequest d : detalles) {
            if (!productosVistos.add(d.idProducto())) {
                throw new SolicitudInvalidaException("El producto " + d.idProducto()
                        + " está repetido en el pedido. Usá un solo renglón con la cantidad total");
            }
        }

        // Se borran los renglones anteriores (orphanRemoval los elimina de la base)
        pedido.getDetalles().clear();

        for (DetallePedidoRequest d : detalles) {
            Producto producto = obtenerProducto(d.idProducto());
            pedido.agregarDetalle(DetallePedido.builder()
                    .producto(producto)
                    .cantidad(d.cantidad())
                    .precioUnitario(producto.getPrecio())
                    .build());
        }
    }

    private Producto elegirProductoDelPago(Pedido pedido, Long idProducto) {
        if (idProducto != null) {
            return obtenerProducto(idProducto);
        }
        if (pedido.getDetalles().size() == 1) {
            return pedido.getDetalles().get(0).getProducto();
        }
        throw new SolicitudInvalidaException(
                "El pedido tiene varios productos: indicá en el pago a qué producto corresponde (idProducto)");
    }

    private void validarPendiente(Pedido pedido, String accion) {
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ConflictoException("No se puede " + accion + " el pedido " + pedido.getIdPedido()
                    + " porque está " + pedido.getEstado());
        }
    }

    private Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pedido con id " + id));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }

    private Cliente obtenerClienteActivo(Long id) {
        Cliente cliente = obtenerCliente(id);
        if (!Boolean.TRUE.equals(cliente.getActivo())) {
            throw new ConflictoException("El cliente " + id + " está dado de baja");
        }
        return cliente;
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }
}
