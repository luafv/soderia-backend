package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Pedido;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdClienteOrderByFechaEntregaDesc(Long idCliente);

    // Hoja de ruta: pedidos de una fecha ordenados por dirección del cliente
    List<Pedido> findByFechaEntregaOrderByClienteDireccionAsc(LocalDate fechaEntrega);

    List<Pedido> findByFechaEntregaAndEstadoOrderByClienteDireccionAsc(LocalDate fechaEntrega, EstadoPedido estado);

    boolean existsByClienteIdClienteAndFechaEntrega(Long idCliente, LocalDate fechaEntrega);

    boolean existsByClienteIdClienteAndFechaEntregaAndIdPedidoNot(Long idCliente, LocalDate fechaEntrega, Long idPedido);
}
