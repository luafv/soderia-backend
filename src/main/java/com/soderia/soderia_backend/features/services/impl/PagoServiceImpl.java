package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.features.dtos.request.PagoRequest;
import com.soderia.soderia_backend.features.dtos.response.DeudaProductoResponse;
import com.soderia.soderia_backend.features.dtos.response.DeudaResponse;
import com.soderia.soderia_backend.features.dtos.response.EntregaCuentaResponse;
import com.soderia.soderia_backend.features.dtos.response.EstadoCuentaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.mappers.PagoMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.DetalleVisita;
import com.soderia.soderia_backend.features.models.Pago;
import com.soderia.soderia_backend.features.models.Visita;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.EstadoPago;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import com.soderia.soderia_backend.features.models.enums.MetodoPago;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.DetalleVisitaRepository;
import com.soderia.soderia_backend.features.repositories.PagoRepository;
import com.soderia.soderia_backend.features.repositories.ProductoRepository;
import com.soderia.soderia_backend.features.services.interfaces.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVisitaRepository detalleVisitaRepository;
    private final PagoMapper pagoMapper;

    // ===================== Pagos =====================

    @Override
    public PagoResponse registrar(PagoRequest request) {
        Cliente cliente = obtenerCliente(request.idCliente());
        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + request.idProducto()));
        LocalDate fecha = request.fechaPago() != null ? request.fechaPago() : LocalDate.now();
        return registrarPago(cliente, producto, request.monto(), request.metodoPago(), fecha, null);
    }

    @Override
    public PagoResponse registrarPago(Cliente cliente, Producto producto, BigDecimal monto,
                                      MetodoPago metodoPago, LocalDate fecha, Visita visita) {
        // La plata se convierte a unidades con el precio de HOY del producto.
        // Ej: $68.500 con el sifón a $1.000 = 68,5 sifones pagados.
        BigDecimal unidades = monto.divide(producto.getPrecio(), 4, RoundingMode.HALF_UP);

        Pago pago = Pago.builder()
                .cliente(cliente)
                .producto(producto)
                .visita(visita)
                .monto(monto)
                .cantidad(unidades)
                .metodoPago(metodoPago)
                .fechaPago(fecha)
                .build();
        return pagoMapper.toResponse(pagoRepository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorCliente(Long idCliente) {
        obtenerCliente(idCliente);
        return pagoRepository.findByClienteIdClienteOrderByFechaPagoDescIdPagoDesc(idCliente).stream()
                .map(pagoMapper::toResponse)
                .toList();
    }

    @Override
    public void eliminar(Long idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pago con id " + idPago));
        pagoRepository.delete(pago);
    }

    // ===================== Deuda y estado de cuenta =====================

    @Override
    @Transactional(readOnly = true)
    public DeudaResponse calcularDeuda(Long idCliente) {
        // Sale del mismo cálculo que el estado de cuenta, así nunca pueden dar distinto
        EstadoCuentaResponse cuenta = estadoDeCuenta(idCliente);
        return new DeudaResponse(cuenta.idCliente(), cuenta.cliente(), cuenta.resumen(), cuenta.totalEnPesos());
    }

    /*
     * Cómo se aplican los pagos a las entregas (todo en unidades del producto):
     *  1. Un pago cobrado en una entrega cubre primero ESA entrega. Si sobra, va al pozo.
     *  2. Los pagos sueltos van directo al pozo.
     *  3. El pozo cubre las entregas impagas de la más vieja a la más nueva.
     *  4. Si después de cubrir todo sobra, es saldo a favor del cliente.
     * Nada de esto se guarda: se recalcula cada vez, así siempre coincide con los datos.
     */
    @Override
    @Transactional(readOnly = true)
    public EstadoCuentaResponse estadoDeCuenta(Long idCliente) {
        Cliente cliente = obtenerCliente(idCliente);

        List<DetalleVisita> entregas = detalleVisitaRepository.buscarPorClienteYEstado(idCliente, EstadoVisita.ENTREGADA);
        List<Pago> pagos = pagoRepository.findByClienteIdClienteOrderByFechaPagoAscIdPagoAsc(idCliente);

        Map<Long, BigDecimal> cubierto = new HashMap<>();   // idDetalleVisita -> unidades cubiertas
        Map<Long, BigDecimal> pozo = new HashMap<>();       // idProducto -> unidades pagadas sin aplicar
        Map<Long, Producto> productos = new LinkedHashMap<>();

        entregas.forEach(d -> productos.putIfAbsent(d.getProducto().getIdProducto(), d.getProducto()));
        pagos.forEach(p -> productos.putIfAbsent(p.getProducto().getIdProducto(), p.getProducto()));

        // 1 y 2. Pagos vinculados a una entrega y pagos sueltos
        for (Pago pago : pagos) {
            Long idProducto = pago.getProducto().getIdProducto();
            BigDecimal restante = pago.getCantidad();

            if (pago.getVisita() != null) {
                DetalleVisita detalle = buscarRenglon(entregas, pago.getVisita().getIdVisita(), idProducto);
                if (detalle != null) {
                    restante = restante.subtract(aplicar(detalle, restante, cubierto));
                }
            }
            pozo.merge(idProducto, restante, BigDecimal::add);
        }

        // 3. El pozo cubre las entregas impagas, de la más vieja a la más nueva
        for (DetalleVisita detalle : entregas) {
            Long idProducto = detalle.getProducto().getIdProducto();
            BigDecimal disponible = pozo.getOrDefault(idProducto, BigDecimal.ZERO);
            if (disponible.signum() > 0) {
                pozo.put(idProducto, disponible.subtract(aplicar(detalle, disponible, cubierto)));
            }
        }

        // Detalle de cada entrega (de la más nueva a la más vieja)
        List<EntregaCuentaResponse> detalleEntregas = new ArrayList<>();
        for (int i = entregas.size() - 1; i >= 0; i--) {
            DetalleVisita d = entregas.get(i);
            BigDecimal cantidad = BigDecimal.valueOf(d.getCantidad());
            BigDecimal cubiertas = cubierto.getOrDefault(d.getIdDetalleVisita(), BigDecimal.ZERO);
            BigDecimal pendientes = cantidad.subtract(cubiertas);

            EstadoPago estado;
            if (pendientes.signum() <= 0) {
                estado = EstadoPago.PAGADA;
            } else if (cubiertas.signum() > 0) {
                estado = EstadoPago.PARCIAL;
            } else {
                estado = EstadoPago.IMPAGA;
            }

            detalleEntregas.add(new EntregaCuentaResponse(
                    d.getVisita().getIdVisita(),
                    d.getVisita().getFecha(),
                    d.getProducto().getIdProducto(),
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    cubiertas,
                    pendientes,
                    enPesos(pendientes, d.getProducto().getPrecio()),
                    estado
            ));
        }

        // Resumen por producto y total (negativo = saldo a favor)
        List<DeudaProductoResponse> resumen = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Producto producto : productos.values()) {
            Long idProducto = producto.getIdProducto();
            long entregadas = entregas.stream()
                    .filter(d -> d.getProducto().getIdProducto().equals(idProducto))
                    .mapToLong(DetalleVisita::getCantidad)
                    .sum();
            BigDecimal pagadas = pagos.stream()
                    .filter(p -> p.getProducto().getIdProducto().equals(idProducto))
                    .map(Pago::getCantidad)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal adeudadas = BigDecimal.valueOf(entregadas).subtract(pagadas);
            BigDecimal pesos = enPesos(adeudadas, producto.getPrecio());

            resumen.add(new DeudaProductoResponse(
                    idProducto, producto.getNombre(), entregadas, pagadas, adeudadas, producto.getPrecio(), pesos));
            total = total.add(pesos);
        }

        return new EstadoCuentaResponse(
                cliente.getIdCliente(),
                cliente.getNombre() + " " + cliente.getApellido(),
                cliente.getTelefono(),
                LocalDate.now(),
                detalleEntregas,
                resumen,
                total
        );
    }

    // ===================== Auxiliares =====================

    // Cubre lo que pueda del renglón con las unidades disponibles. Devuelve cuánto usó.
    private BigDecimal aplicar(DetalleVisita detalle, BigDecimal disponible, Map<Long, BigDecimal> cubierto) {
        BigDecimal yaCubierto = cubierto.getOrDefault(detalle.getIdDetalleVisita(), BigDecimal.ZERO);
        BigDecimal falta = BigDecimal.valueOf(detalle.getCantidad()).subtract(yaCubierto);
        if (falta.signum() <= 0 || disponible.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal usado = falta.min(disponible);
        cubierto.put(detalle.getIdDetalleVisita(), yaCubierto.add(usado));
        return usado;
    }

    private DetalleVisita buscarRenglon(List<DetalleVisita> entregas, Long idVisita, Long idProducto) {
        return entregas.stream()
                .filter(d -> d.getVisita().getIdVisita().equals(idVisita)
                        && d.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
    }

    private BigDecimal enPesos(BigDecimal unidades, BigDecimal precio) {
        return unidades.multiply(precio).setScale(2, RoundingMode.HALF_UP);
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }
}
