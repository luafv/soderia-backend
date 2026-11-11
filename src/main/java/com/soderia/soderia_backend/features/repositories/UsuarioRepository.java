package com.soderia.soderia_backend.features.repositories;

import com.soderia.soderia_backend.features.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


// Se va a usar en el login (todavía no implementado)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);
}
