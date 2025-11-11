package br.com.rzaninelli.gestor.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BibliotecaException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, Object> detalhes;

    public BibliotecaException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.detalhes = new HashMap<>();
    }

    public BibliotecaException(String message, String errorCode, HttpStatus httpStatus, Map<String, Object> detalhes) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.detalhes = detalhes;
    }
}
