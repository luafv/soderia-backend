package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Visita;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitaRepository extends JpaRepository<Visita, Long> {

    List<Visita> findByClienteIdClienteOrderByFechaDesc(Long idCliente);

    // Visitas de un cliente en un estado (ej: las PENDIENTES)
    List<Visita> findByClienteIdClienteAndEstadoOrderByFechaAsc(Long idCliente, EstadoVisita estado);

    // Hoja de ruta: visitas de una fecha ordenadas por dirección del cliente
    List<Visita> findByFechaOrderByClienteDireccionAsc(LocalDate fecha);

    List<Visita> findByFechaAndEstadoOrderByClienteDireccionAsc(LocalDate fecha, EstadoVisita estado);

    boolean existsByClienteIdClienteAndFecha(Long idCliente, LocalDate fecha);
}
