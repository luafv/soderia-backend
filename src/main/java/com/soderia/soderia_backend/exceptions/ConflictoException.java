package com.soderia.soderia_backend.exceptions;

// Se lanza cuando la operación choca con datos existentes (ej: nombre repetido) -> 409
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
