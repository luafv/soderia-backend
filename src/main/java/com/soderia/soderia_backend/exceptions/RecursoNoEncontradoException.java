package com.soderia.soderia_backend.exceptions;

// Se lanza cuando se busca algo que no existe -> 404
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
