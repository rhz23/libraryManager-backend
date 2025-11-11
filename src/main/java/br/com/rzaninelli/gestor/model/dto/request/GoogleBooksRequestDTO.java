package br.com.rzaninelli.gestor.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GoogleBooksRequestDTO(@NotBlank(message = "Query é obrigatória") String query, String autor, String titulo, String isbn,
                                    @Min(1) @Max(40) Integer maxResults, @Min(0) Integer startIndex, String orderBy) {
    public GoogleBooksRequestDTO {
        if (maxResults == null) {
            maxResults = 10;
        }
        if (startIndex == null) {
            startIndex = 0;
        }
    }
}
