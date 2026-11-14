package com.soderia.soderia_backend.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.soderia.soderia_backend.features.models.Usuario;
import com.soderia.soderia_backend.features.repositories.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    // Se ejecuta una vez por petición para comprobar si existe un token JWT.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Obtiene el encabezado Authorization de la petición.
        final String authHeader = request.getHeader("Authorization");

        // Si no hay token o no tiene el formato Bearer, continúa sin autenticar.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extrae el token quitando el prefijo "Bearer ".
        final String token = authHeader.substring(7);

        // Obtiene el nombre de usuario guardado en el token.
        final String username = jwtService.obtenerUsuario(token);

        // Si hay un usuario identificado y todavía no está autenticado,
        // busca sus datos en la base de datos.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Usuario usuario = usuarioRepository.findByUsuario(username).orElse(null);

            // Comprueba que el usuario exista y que el token sea válido.
            if (usuario != null && jwtService.esTokenValido(token, usuario)) {

                // Crea la autenticación que utilizará Spring Security.
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(usuario.getUsuario())
                        .password(usuario.getPassword())
                        .roles(usuario.getRol().name())
                        .build();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Guarda la autenticación para el resto de la petición.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continúa con los siguientes filtros de Spring Security.
        filterChain.doFilter(request, response);
    }
}
