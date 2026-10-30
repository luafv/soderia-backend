package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByClienteIdClienteOrderByFechaPagoDescIdPagoDesc(Long idCliente);

    // Del más viejo al más nuevo: para aplicar los pagos a las entregas en orden
    List<Pago> findByClienteIdClienteOrderByFechaPagoAscIdPagoAsc(Long idCliente);

    // Pagos cobrados en una fecha (en entregas o sueltos): para el reporte del día
    List<Pago> findByFechaPago(LocalDate fechaPago);
}
