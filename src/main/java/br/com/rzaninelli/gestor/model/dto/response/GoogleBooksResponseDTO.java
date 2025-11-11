package br.com.rzaninelli.gestor.model.dto.response;

import java.time.LocalDate;

public record GoogleBooksResponseDTO(String id, String titulo, String autor, String isbn, LocalDate dataPublicacao,
        String categoria, String descricao, String imagemUrl, Integer numeroPaginas, String idioma) {}

