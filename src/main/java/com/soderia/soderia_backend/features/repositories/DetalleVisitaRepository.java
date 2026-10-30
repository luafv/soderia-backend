package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.DetalleVisita;
import com.soderia.soderia_backend.features.models.enums.EstadoVisita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVisitaRepository extends JpaRepository<DetalleVisita, Long> {

    // Renglones de las visitas de un cliente en un estado, del más viejo al más nuevo
    @Query("""
            SELECT d FROM DetalleVisita d
            WHERE d.visita.cliente.idCliente = :idCliente
              AND d.visita.estado = :estado
            ORDER BY d.visita.fecha ASC, d.visita.idVisita ASC
            """)
    List<DetalleVisita> buscarPorClienteYEstado(@Param("idCliente") Long idCliente,
                                                @Param("estado") EstadoVisita estado);
}
