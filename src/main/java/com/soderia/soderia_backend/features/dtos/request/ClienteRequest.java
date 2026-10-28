package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;

// Datos para crear o actualizar un cliente
public record ClienteRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
        String apellido,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
        String direccion,

        // Opcional
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 150, message = "El email no puede superar los 150 caracteres")
        String email,

        // Opcional. Solo números, espacios, guiones y + (ej: +54 9 11 1234-5678)
        @Pattern(regexp = "^[0-9+\\-\\s]{6,30}$", message = "El teléfono solo puede tener números, espacios, guiones y +")
        String telefono,

        // Opcional. Valores: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
        DayOfWeek diaEntrega,

        // Opcional. Envases que tiene el cliente en su casa (al crear: 0 si no se manda; al modificar: no cambia)
        @PositiveOrZero(message = "Los sifones en casa no pueden ser negativos")
        Integer sifonesEnCasa
) {
}
