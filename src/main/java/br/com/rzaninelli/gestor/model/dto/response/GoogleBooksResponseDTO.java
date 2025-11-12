package br.com.rzaninelli.gestor.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBooksResponseDTO {

    private String id;
    private String titulo;
    private String autor;
    private String isbn;
    private LocalDate dataPublicacao;
    private String categoria;
    private String descricao;
    private String imagemUrl;
    private Integer numeroPaginas;
    private String idioma;
}