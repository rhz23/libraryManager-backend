package br.com.rzaninelli.gestor.exception.custom;

import org.springframework.http.HttpStatus;

public class DatabaseException extends BibliotecaException {

    public DatabaseException(String message) {
        super(message, "DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, "DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}
