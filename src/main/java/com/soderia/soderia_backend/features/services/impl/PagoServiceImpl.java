package com.soderia.soderia_backend.features.services.impl;

import com.soderia.soderia_backend.exceptions.RecursoNoEncontradoException;
import com.soderia.soderia_backend.features.dtos.request.PagoRequest;
import com.soderia.soderia_backend.features.dtos.response.DeudaProductoResponse;
import com.soderia.soderia_backend.features.dtos.response.DeudaResponse;
import com.soderia.soderia_backend.features.dtos.response.PagoResponse;
import com.soderia.soderia_backend.features.mappers.PagoMapper;
import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.Pago;
import com.soderia.soderia_backend.features.models.Producto;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import com.soderia.soderia_backend.features.models.enums.MetodoPago;
import com.soderia.soderia_backend.features.repositories.ClienteRepository;
import com.soderia.soderia_backend.features.repositories.DetallePedidoRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoMapper pagoMapper;

    @Override
    public PagoResponse registrar(PagoRequest request) {
        Cliente cliente = obtenerCliente(request.idCliente());
        Producto producto = obtenerProducto(request.idProducto());
        LocalDate fecha = request.fechaPago() != null ? request.fechaPago() : LocalDate.now();
        return registrarPago(cliente, producto, request.monto(), request.metodoPago(), fecha);
    }

    @Override
    public PagoResponse registrarPago(Cliente cliente, Producto producto, BigDecimal monto,
                                      MetodoPago metodoPago, LocalDate fecha) {
        // La plata se convierte a unidades con el precio de HOY del producto.
        // Ej: $68.500 con el sifón a $1.000 = 68,5 sifones pagados.
        BigDecimal unidades = monto.divide(producto.getPrecio(), 4, RoundingMode.HALF_UP);

        Pago pago = Pago.builder()
                .cliente(cliente)
                .producto(producto)
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

    @Override
    @Transactional(readOnly = true)
    public DeudaResponse calcularDeuda(Long idCliente) {
        Cliente cliente = obtenerCliente(idCliente);

        List<DeudaProductoResponse> productos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Producto producto : productoRepository.findAll()) {
            Long entregadas = detallePedidoRepository.sumarCantidadEntregada(
                    idCliente, producto.getIdProducto(), EstadoPedido.ENTREGADO);
            BigDecimal pagadas = pagoRepository.sumarCantidadPagada(idCliente, producto.getIdProducto());

            long totalEntregadas = entregadas != null ? entregadas : 0L;
            BigDecimal totalPagadas = pagadas != null ? pagadas : BigDecimal.ZERO;

            // Solo se muestran los productos con movimientos
            if (totalEntregadas == 0 && totalPagadas.signum() == 0) {
                continue;
            }

            BigDecimal adeudadas = BigDecimal.valueOf(totalEntregadas).subtract(totalPagadas);
            BigDecimal enPesos = adeudadas.multiply(producto.getPrecio()).setScale(2, RoundingMode.HALF_UP);

            productos.add(new DeudaProductoResponse(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    totalEntregadas,
                    totalPagadas,
                    adeudadas,
                    producto.getPrecio(),
                    enPesos
            ));
            total = total.add(enPesos);
        }

        return new DeudaResponse(
                cliente.getIdCliente(),
                cliente.getNombre() + " " + cliente.getApellido(),
                productos,
                total
        );
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
