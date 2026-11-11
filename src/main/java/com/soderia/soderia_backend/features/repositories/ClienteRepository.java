package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Cliente;
import com.soderia.soderia_backend.features.models.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Clientes activos a visitar en un día de reparto (para armar el recorrido)
    List<Cliente> findByDiaEntregaAndActivoTrueOrderByDireccionAsc(DiaSemana diaEntrega);

    // Búsqueda con filtros opcionales: texto vacío = sin filtro
    // - buscar: nombre o apellido
    // - direccion: parte de la dirección
    @Query("""
            SELECT c FROM Cliente c
            WHERE (:incluirInactivos = true OR c.activo = true)
              AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :buscar, '%'))
                   OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :buscar, '%')))
              AND LOWER(c.direccion) LIKE LOWER(CONCAT('%', :direccion, '%'))
            ORDER BY c.apellido, c.nombre
            """)
    List<Cliente> buscar(@Param("buscar") String buscar,
                         @Param("direccion") String direccion,
                         @Param("incluirInactivos") boolean incluirInactivos);
}
