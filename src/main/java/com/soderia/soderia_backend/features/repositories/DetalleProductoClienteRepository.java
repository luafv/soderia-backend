package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.DetalleProductoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleProductoClienteRepository extends JpaRepository<DetalleProductoCliente, Long> {

    List<DetalleProductoCliente> findByClienteIdCliente(Long idCliente);

    Optional<DetalleProductoCliente> findByClienteIdClienteAndProductoIdProducto(Long idCliente, Long idProducto);

    boolean existsByClienteIdClienteAndProductoIdProducto(Long idCliente, Long idProducto);

    // Borra los productos fijos de un cliente (se usa antes de borrar al cliente)
    void deleteByClienteIdCliente(Long idCliente);
}
