package br.com.rzaninelli.gestor.exception.custom;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class BusinessException extends BibliotecaException {

    public BusinessException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, Map<String, Object> details) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.BAD_REQUEST, details);
    }
}
