package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Clientes a visitar en un día de reparto (para armar el recorrido)
    List<Cliente> findByDiaEntregaOrderByDireccionAsc(DayOfWeek diaEntrega);

    // Búsqueda por nombre o apellido
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrderByApellidoAscNombreAsc(
            String nombre, String apellido);
}
