package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    // Para validar nombre repetido al actualizar, sin contar al propio producto
    boolean existsByNombreIgnoreCaseAndIdProductoNot(String nombre, Long idProducto);
}
