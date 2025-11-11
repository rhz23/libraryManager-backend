package br.com.rzaninelli.gestor.exception.dto;

import br.com.rzaninelli.gestor.exception.custom.BibliotecaException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, Object> details;

    public static ErrorResponse of(HttpStatus status, String message, String errorCode) {

        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(BibliotecaException exception, String path) {

        return ErrorResponse.builder()
                .status(exception.getHttpStatus().value())
                .error(exception.getHttpStatus().getReasonPhrase())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .path(path)
                .timestamp(LocalDateTime.now())
                .details(exception.getDetalhes())
                .build();
    }
}
