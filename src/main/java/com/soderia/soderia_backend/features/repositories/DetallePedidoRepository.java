package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.DetallePedido;
import com.soderia.soderia_backend.features.models.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoIdPedido(Long idPedido);

    // Total de unidades de un producto que se le entregaron a un cliente (para calcular la deuda)
    @Query("""
            SELECT COALESCE(SUM(d.cantidad), 0)
            FROM DetallePedido d
            WHERE d.pedido.cliente.idCliente = :idCliente
              AND d.producto.idProducto = :idProducto
              AND d.pedido.estado = :estado
            """)
    Long sumarCantidadEntregada(@Param("idCliente") Long idCliente,
                                @Param("idProducto") Long idProducto,
                                @Param("estado") EstadoPedido estado);
}
