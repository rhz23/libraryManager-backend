package br.com.rzaninelli.gestor.exception.custom;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ExternalApiException extends BibliotecaException {

    public ExternalApiException(String message) {
        super(message, "EXTERNAL_API_ERROR", HttpStatus.BAD_GATEWAY);
    }

    public ExternalApiException(String apiName, String message) {
        super(
                String.format("Erro ao comunicar com %s: %s", apiName, message),
                "EXTERNAL_API_ERROR",
                HttpStatus.BAD_GATEWAY,
                Map.of("apiName", apiName)
        );
    }
}
