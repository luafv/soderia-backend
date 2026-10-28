package com.soderia.soderia_backend.features.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// Corrección manual de los envases que tiene el cliente en su casa
public record SifonesRequest(

        @NotNull(message = "La cantidad de sifones en casa es obligatoria")
        @PositiveOrZero(message = "Los sifones en casa no pueden ser negativos")
        Integer sifonesEnCasa
) {
}
