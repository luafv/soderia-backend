package com.soderia.soderia_backend.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soderia.soderia_backend.features.models.Usuario;
import com.soderia.soderia_backend.features.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
   
    private final UsuarioRepository usuarioRepository;

    // Busca un usuario en la base de datos a partir de su nombre de usuario.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró el usuario: " + username));

        // Convierte nuestra entidad Usuario al formato que utiliza Spring Security.
        return User.withUsername(usuario.getUsuario())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .build();
    }
}
