package com.soderia.soderia_backend.config;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;

import javax.crypto.SecretKey;

import com.soderia.soderia_backend.features.models.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Genera un token JWT con el usuario y su rol.
    public String generarToken(Usuario usuario) {

        return Jwts.builder()
                .subject(usuario.getUsuario())
                .claim("rol", usuario.getRol().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Obtiene el nombre de usuario guardado en el token.
    public String obtenerUsuario(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Comprueba si el token pertenece al usuario y sigue vigente.
    public boolean esTokenValido(String token, Usuario usuario) {
        try {
            Claims claims = obtenerClaims(token);

            return claims.getSubject().equals(usuario.getUsuario())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Convierte la clave secreta en una clave criptográfica para firmar.
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Lee y verifica la firma del token antes de devolver sus datos.
    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
