package com.soderia.soderia_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Datos que aparecen arriba de todo en Swagger (http://localhost:8080/swagger-ui.html)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI soderiaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Sodería - API")
                .version("0.0.1")
                .description("API para gestionar el reparto de una sodería: productos, clientes, "
                        + "visitas y entregas del día, pagos, deuda en sifones y recorridos."))

                         // Define el esquema de autenticación JWT para Swagger.
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
