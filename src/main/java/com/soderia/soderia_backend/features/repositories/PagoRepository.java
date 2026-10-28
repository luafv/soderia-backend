package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByClienteIdClienteOrderByFechaPagoDescIdPagoDesc(Long idCliente);

    // Total de unidades de un producto que pagó un cliente (null si no hay pagos)
    @Query("""
            SELECT SUM(p.cantidad)
            FROM Pago p
            WHERE p.cliente.idCliente = :idCliente
              AND p.producto.idProducto = :idProducto
            """)
    BigDecimal sumarCantidadPagada(@Param("idCliente") Long idCliente,
                                   @Param("idProducto") Long idProducto);
}
