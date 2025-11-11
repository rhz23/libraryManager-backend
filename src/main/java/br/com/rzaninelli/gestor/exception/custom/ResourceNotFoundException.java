package br.com.rzaninelli.gestor.exception.custom;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ResourceNotFoundException extends BibliotecaException{

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(
                String.format("%s com ID %d não encontrado", resourceName, id),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                Map.of("resourceName", resourceName, "id", id)
        );
    }
}
