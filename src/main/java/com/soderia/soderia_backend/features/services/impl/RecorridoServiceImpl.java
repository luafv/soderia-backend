package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.ConflictoException;
import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.exceptions.SolicitudInvalidaException;
import com.soderia.soderia_backend.features.dtos.request.GenerarPedidosRequest;
import com.soderia.soderia_backend.features.dtos.request.IniciarRecorridoRequest;
import com.soderia.soderia_backend.features.dtos.response.ReporteRecorridoResponse;
import com.soderia.soderia_backend.features.dtos.response.ResumenRecorridoResponse;
import com.soderia.soderia_backend.features.dtos.response.VisitaReporteResponse;
import com.soderia.soderia_backend.features.models.DetallePedido;
import com.soderia.soderia_backend.features.models.Pago;
import com.soderia.soderia_backend.features.models.Pedido;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.Recorrido;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import com.soderia.soderia_backend.features.models.enums.EstadoRecorrido;
import com.soderia.soderia_backend.features.models.enums.MetodoPago;
import com.soderia.soderia_backend.features.repositories.PagoRepository;
import com.soderia.soderia_backend.features.repositories.PedidoRepository;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.repositories.RecorridoRepository;
import com.soderia.soderia_backend.features.services.interfaces.PedidoService;
import com.soderia.soderia_backend.features.services.interfaces.RecorridoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class RecorridoServiceImpl implements RecorridoService {

    private final RecorridoRepository recorridoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final PedidoService pedidoService;

    // ===================== Iniciar y finalizar =====================

    @Override
    public ReporteRecorridoResponse iniciar(IniciarRecorridoRequest request) {
        LocalDate fecha = request.fecha() != null ? request.fecha() : LocalDate.now();
        if (recorridoRepository.existsByFecha(fecha)) {
            throw new ConflictoException("Ya hay un recorrido para el " + fecha);
        }
        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + request.idProducto()));

        // 1. Stock del camión (HU-07)
        producto.setStock(request.stock());

        // 2. Se abre el día
        Recorrido recorrido = recorridoRepository.save(Recorrido.builder()
                .fecha(fecha)
                .producto(producto)
                .stockInicial(request.stock())
                .estado(EstadoRecorrido.ABIERTO)
                .fechaInicio(LocalDateTime.now())
                .build());

        // 3. Visitas del día para los clientes activos de ese día (HU-08)
        pedidoService.generarPedidosDelDia(new GenerarPedidosRequest(fecha));

        return armarReporte(recorrido);
    }

    @Override
    public ReporteRecorridoResponse finalizar(LocalDate fecha) {
        Recorrido recorrido = obtener(fecha);
        if (recorrido.getEstado() == EstadoRecorrido.CERRADO) {
            throw new ConflictoException("El recorrido del " + fecha + " ya está cerrado");
        }

        // Los clientes que no se visitaron quedan como ausentes
        for (Pedido pedido : pedidoRepository.findByFechaEntregaAndEstadoOrderByClienteDireccionAsc(fecha, EstadoPedido.PENDIENTE)) {
            pedido.setEstado(EstadoPedido.AUSENTE);
        }

        recorrido.setEstado(EstadoRecorrido.CERRADO);
        recorrido.setFechaCierre(LocalDateTime.now());
        return armarReporte(recorrido);
    }

    // ===================== Consultas =====================

    @Override
    @Transactional(readOnly = true)
    public ReporteRecorridoResponse reporte(LocalDate fecha) {
        return armarReporte(obtener(fecha));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumenRecorridoResponse> historial(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        return recorridoRepository.findByFechaBetweenOrderByFechaAsc(desde, hasta).stream()
                .map(r -> {
                    ReporteRecorridoResponse rep = armarReporte(r);
                    return new ResumenRecorridoResponse(rep.fecha(), rep.estado(), rep.sifonesEntregados(),
                            rep.totalVendido(), rep.totalCobrado());
                })
                .toList();
    }

    // ===================== Reporte =====================

    private ReporteRecorridoResponse armarReporte(Recorrido recorrido) {
        LocalDate fecha = recorrido.getFecha();
        Producto producto = recorrido.getProducto();
        List<Pedido> pedidos = pedidoRepository.findByFechaEntregaOrderByClienteDireccionAsc(fecha);
        List<Pago> pagos = pagoRepository.findByFechaPago(fecha);

        int entregados = 0, ausentes = 0, pendientes = 0, sifones = 0, vacios = 0, delProducto = 0;
        BigDecimal vendido = BigDecimal.ZERO;
        List<VisitaReporteResponse> visitas = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            int dejados = 0;
            BigDecimal importe = BigDecimal.ZERO;
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                entregados++;
                for (DetallePedido d : pedido.getDetalles()) {
                    dejados += d.getCantidad();
                    importe = importe.add(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
                    if (d.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                        delProducto += d.getCantidad();
                    }
                }
                vacios += pedido.getVaciosRetirados() != null ? pedido.getVaciosRetirados() : 0;
            } else if (pedido.getEstado() == EstadoPedido.AUSENTE) {
                ausentes++;
            } else {
                pendientes++;
            }
            sifones += dejados;
            vendido = vendido.add(importe);

            BigDecimal cobrado = pagos.stream()
                    .filter(p -> p.getPedido() != null && Objects.equals(p.getPedido().getIdPedido(), pedido.getIdPedido()))
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            visitas.add(new VisitaReporteResponse(
                    pedido.getIdPedido(),
                    pedido.getCliente().getIdCliente(),
                    pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido(),
                    pedido.getCliente().getDireccion(),
                    pedido.getEstado(),
                    dejados,
                    pedido.getVaciosRetirados(),
                    importe,
                    cobrado
            ));
        }

        BigDecimal efectivo = sumar(pagos, MetodoPago.EFECTIVO);
        BigDecimal transferencia = sumar(pagos, MetodoPago.TRANSFERENCIA);
        BigDecimal cobrado = efectivo.add(transferencia);

        return new ReporteRecorridoResponse(
                fecha,
                recorrido.getEstado(),
                recorrido.getFechaInicio(),
                recorrido.getFechaCierre(),
                producto.getIdProducto(),
                producto.getNombre(),
                recorrido.getStockInicial(),
                recorrido.getStockInicial() - delProducto,
                pedidos.size(),
                entregados,
                ausentes,
                pendientes,
                sifones,
                vacios,
                vendido,
                efectivo,
                transferencia,
                cobrado,
                vendido.subtract(cobrado),
                visitas
        );
    }

    private BigDecimal sumar(List<Pago> pagos, MetodoPago metodo) {
        return pagos.stream()
                .filter(p -> p.getMetodoPago() == metodo)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Recorrido obtener(LocalDate fecha) {
        return recorridoRepository.findByFecha(fecha)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay recorrido para el " + fecha));
    }
}
