package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.ConflictoException;
import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.exceptions.SolicitudInvalidaException;
import com.soderia.soderia_backend.features.dtos.request.DetalleVisitaRequest;
import com.soderia.soderia_backend.features.dtos.request.EntregaRequest;
import com.soderia.soderia_backend.features.dtos.request.GenerarVisitasRequest;
import com.soderia.soderia_backend.features.dtos.request.PagoEntregaRequest;
import com.soderia.soderia_backend.features.dtos.response.EntregaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.dtos.response.VisitaResponse;
import com.soderia.soderia_backend.features.mappers.VisitaMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.DetalleVisita;
import com.soderia.soderia_backend.features.models.Visita;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import com.soderia.soderia_backend.features.models.enums.EstadoRecorrido;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.VisitaRepository;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.repositories.RecorridoRepository;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import com.soderia.soderia_backend.features.services.interfaces.VisitaService;
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
public class VisitaServiceImpl implements VisitaService {

    private final VisitaRepository visitaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final RecorridoRepository recorridoRepository;
    private final PagoService pagoService;
    private final VisitaMapper visitaMapper;

    // ===================== Consultas =====================

    @Override
    @Transactional(readOnly = true)
    public List<VisitaResponse> listarPorFecha(LocalDate fecha, EstadoVisita estado) {
        List<Visita> visitas = (estado == null)
                ? visitaRepository.findByFechaOrderByClienteDireccionAsc(fecha)
                : visitaRepository.findByFechaAndEstadoOrderByClienteDireccionAsc(fecha, estado);
        return visitas.stream().map(visitaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitaResponse> listarPorCliente(Long idCliente) {
        obtenerCliente(idCliente);
        return visitaRepository.findByClienteIdClienteOrderByFechaDesc(idCliente).stream()
                .map(visitaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VisitaResponse buscarPorId(Long id) {
        return visitaMapper.toResponse(obtenerVisita(id));
    }

    // ===================== Visitas del día =====================

    @Override
    public List<VisitaResponse> generarVisitasDelDia(GenerarVisitasRequest request) {
        // HU-08: si no se manda fecha, se generan las de hoy
        LocalDate fecha = (request == null || request.fecha() == null) ? LocalDate.now() : request.fecha();
        validarDiaAbierto(fecha);
        List<VisitaResponse> creadas = new ArrayList<>();

        for (Cliente cliente : clienteRepository.findByDiaEntregaAndActivoTrueOrderByDireccionAsc(fecha.getDayOfWeek())) {
            // Si ya tiene visita ese día, no se duplica
            if (visitaRepository.existsByClienteIdClienteAndFecha(cliente.getIdCliente(), fecha)) {
                continue;
            }
            // Visita pendiente SIN cantidades: lo que se deja se registra recién en la casa del cliente.
            // La respuesta trae un "sugerido" = los envases que el cliente tiene en casa.
            Visita visita = Visita.builder()
                    .cliente(cliente)
                    .fecha(fecha)
                    .build();
            creadas.add(visitaMapper.toResponse(visitaRepository.save(visita)));
        }
        return creadas;
    }

    // ===================== Entrega =====================

    @Override
    public EntregaResponse entregar(Long id, EntregaRequest request) {
        Visita visita = obtenerVisita(id);
        validarDiaAbierto(visita.getFecha());
        validarPendiente(visita, "entregar");

        // 1. Lo que el repartidor dejó en la casa. Si lo informa, reemplaza lo que tuviera la visita.
        if (request != null && request.detalles() != null && !request.detalles().isEmpty()) {
            cargarDetalles(visita, request.detalles());
        }
        // Una visita generada del día no tiene cantidades: hay que decir cuánto se dejó
        if (visita.getDetalles().isEmpty()) {
            throw new SolicitudInvalidaException("Indicá cuántos sifones le dejaste al cliente (detalles). "
                    + "Sugerido: " + visita.getCliente().getSifonesEnCasa()
                    + ". Si no estaba, marcá la visita como ausente");
        }

        // 2. Verificar stock de todo antes de descontar nada
        for (DetalleVisita detalle : visita.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new ConflictoException("Stock insuficiente de '" + producto.getNombre() + "': hay "
                        + producto.getStock() + " y se quieren entregar " + detalle.getCantidad());
            }
        }

        // 3. HU-09: envases en casa del cliente = antes + llenos que deja - vacíos que se lleva.
        //    Si no se informan los vacíos, se asume el cambio: se lleva tantos vacíos como llenos deja,
        //    pero nunca más de los envases que tenía el cliente.
        Cliente cliente = visita.getCliente();
        int llenos = visita.getDetalles().stream().mapToInt(DetalleVisita::getCantidad).sum();
        int vacios = (request != null && request.vaciosRetirados() != null)
                ? request.vaciosRetirados()
                : Math.min(llenos, cliente.getSifonesEnCasa());
        int enCasa = cliente.getSifonesEnCasa() + llenos - vacios;
        if (enCasa < 0) {
            throw new SolicitudInvalidaException("El cliente tenía " + cliente.getSifonesEnCasa()
                    + " sifones en casa y recibe " + llenos + ": no se le pueden retirar " + vacios + " vacíos");
        }
        cliente.setSifonesEnCasa(enCasa);
        visita.setVaciosRetirados(vacios);

        // 4. Descontar stock y fijar el precio del día de la entrega
        for (DetalleVisita detalle : visita.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
        }

        visita.setEstado(EstadoVisita.ENTREGADA);
        VisitaResponse visitaResponse = visitaMapper.toResponse(visitaRepository.save(visita));

        // 5. Si cobró en el momento, se registra el pago vinculado a esta entrega
        PagoResponse pagoResponse = null;
        if (request != null && request.pago() != null) {
            PagoEntregaRequest pago = request.pago();
            Producto productoPagado = elegirProductoDelPago(visita, pago.idProducto());
            pagoResponse = pagoService.registrarPago(visita.getCliente(), productoPagado,
                    pago.monto(), pago.metodoPago(), visita.getFecha(), visita);
        }

        return new EntregaResponse(visitaResponse, pagoResponse);
    }

    @Override
    public VisitaResponse marcarAusente(Long id) {
        Visita visita = obtenerVisita(id);
        validarDiaAbierto(visita.getFecha());
        validarPendiente(visita, "marcar como ausente");
        visita.setEstado(EstadoVisita.AUSENTE);
        return visitaMapper.toResponse(visitaRepository.save(visita));
    }

    @Override
    public void eliminar(Long id) {
        Visita visita = obtenerVisita(id);
        validarDiaAbierto(visita.getFecha());
        validarPendiente(visita, "eliminar");
        visitaRepository.delete(visita);
    }

    // ===================== Auxiliares =====================

    // Reemplaza los renglones de la visita por los del request
    private void cargarDetalles(Visita visita, List<DetalleVisitaRequest> detalles) {
        Set<Long> productosVistos = new HashSet<>();
        for (DetalleVisitaRequest d : detalles) {
            if (!productosVistos.add(d.idProducto())) {
                throw new SolicitudInvalidaException("El producto " + d.idProducto()
                        + " está repetido en la visita. Usá un solo renglón con la cantidad total");
            }
        }

        // Si se carga de nuevo, se reemplazan los renglones anteriores (orphanRemoval los borra de la base)
        visita.getDetalles().clear();

        for (DetalleVisitaRequest d : detalles) {
            Producto producto = obtenerProducto(d.idProducto());
            visita.agregarDetalle(DetalleVisita.builder()
                    .producto(producto)
                    .cantidad(d.cantidad())
                    .precioUnitario(producto.getPrecio())
                    .build());
        }
    }

    private Producto elegirProductoDelPago(Visita visita, Long idProducto) {
        if (idProducto != null) {
            return obtenerProducto(idProducto);
        }
        if (visita.getDetalles().size() == 1) {
            return visita.getDetalles().get(0).getProducto();
        }
        throw new SolicitudInvalidaException(
                "La visita tiene varios productos: indicá en el pago a qué producto corresponde (idProducto)");
    }

    // Si el recorrido de esa fecha está cerrado, no se puede tocar nada de ese día
    private void validarDiaAbierto(LocalDate fecha) {
        recorridoRepository.findByFecha(fecha)
                .filter(r -> r.getEstado() == EstadoRecorrido.CERRADO)
                .ifPresent(r -> {
                    throw new ConflictoException("El recorrido del " + fecha + " está cerrado. Solo el admin puede reabrirlo");
                });
    }

    private void validarPendiente(Visita visita, String accion) {
        if (visita.getEstado() != EstadoVisita.PENDIENTE) {
            throw new ConflictoException("No se puede " + accion + " la visita " + visita.getIdVisita()
                    + " porque está " + visita.getEstado());
        }
    }

    private Visita obtenerVisita(Long id) {
        return visitaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la visita con id " + id));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }
}
