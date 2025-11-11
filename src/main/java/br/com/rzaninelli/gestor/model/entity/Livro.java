package br.com.rzaninelli.gestor.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 255)
    @Column(nullable = false)
    private String autor;

    @NotBlank(message = "ISBN é obrigatório")
    @Size(min = 10, max = 13)
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Data de publicação é obrigatória")
    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 100)
    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Boolean disponivel = true;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL)
    private List<Emprestimo> emprestimos = new ArrayList<>();
}
