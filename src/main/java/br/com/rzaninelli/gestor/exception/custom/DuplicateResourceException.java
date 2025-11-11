package br.com.rzaninelli.gestor.exception.custom;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class DuplicateResourceException extends BibliotecaException {

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE",  HttpStatus.CONFLICT);
    }

    public DuplicateResourceException(String resourceName, String field, Object value) {
        super(
                String.format("%s com '%s' já existe", resourceName, field, value),
                "DUPLICATE_RESOURCE",
                HttpStatus.CONFLICT,
                Map.of("resourceName", resourceName, "field", field, "value", value)
        );
    }

}
