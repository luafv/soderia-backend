package com.soderia.soderia_backend.exceptions;

// Se lanza cuando la solicitud no tiene sentido para el estado actual de los datos -> 400
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
