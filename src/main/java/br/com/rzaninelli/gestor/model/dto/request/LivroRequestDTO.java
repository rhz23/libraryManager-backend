package br.com.rzaninelli.gestor.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LivroRequestDTO (@NotBlank String titulo, @NotNull String autor,  @NotBlank  @Size(min = 10, max = 17) String isbn,
                               @NotNull LocalDate dataPublicacao, @NotBlank String categoria, String googleBooksId) {
}
