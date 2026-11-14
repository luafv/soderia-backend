package com.soderia.soderia_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soderia.soderia_backend.config.JwtService;
import com.soderia.soderia_backend.features.dtos.request.LoginRequest;
import com.soderia.soderia_backend.features.dtos.response.LoginResponse;
import com.soderia.soderia_backend.features.models.Usuario;
import com.soderia.soderia_backend.features.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Recibe las credenciales, verifica la contraseña y genera el token JWT.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // Busca el usuario por su nombre de usuario.
        Usuario usuario = usuarioRepository.findByUsuario(request.usuario())
                .orElse(null);

        // Comprueba que el usuario exista y que la contraseña sea correcta.
        if (usuario == null || !passwordEncoder.matches(
                request.password(), usuario.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }

        // Genera un token JWT para el usuario autenticado.
        String token = jwtService.generarToken(usuario);

        // Devuelve el token junto con su tipo de autenticación.
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}
