package br.com.rzaninelli.gestor.model.dto.response;

import java.time.LocalDate;

public record LivroResponseDTO (Long id, String titulo, String autor, String isbn,
                                LocalDate dataPublicacao, String categoria, Boolean disponivel) {
}
