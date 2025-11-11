package br.com.rzaninelli.gestor.exception.custom;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends BibliotecaException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    public ValidationException(Map<String, String> fieldErrors) {
        super(
                "Erro de validação nos campos",
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST,
                new HashMap<>(fieldErrors)
        );
    }
}
