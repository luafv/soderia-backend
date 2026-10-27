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

    List<Pedido> findByFechaEntrega(LocalDate fechaEntrega);

    List<Pedido> findByFechaEntregaAndEstado(LocalDate fechaEntrega, EstadoPedido estado);
}
