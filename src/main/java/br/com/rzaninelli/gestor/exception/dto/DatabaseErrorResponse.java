package br.com.rzaninelli.gestor.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatabaseErrorResponse {

    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String sqlState;
    private String constraintName;
    private LocalDateTime timestamp;

    public static DatabaseErrorResponse of(SQLException ex) {
        return DatabaseErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Erro ao executar operação no banco de dados")
                .errorCode("DATABASE_ERROR")
                .sqlState(ex.getSQLState())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
