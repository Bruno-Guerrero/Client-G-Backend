package org.guerrero.bruno.clientg.exceptions;

//Operación que viola una regla de negocio (p. ej. editar un pago anulado)
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) { super(message); }
}
