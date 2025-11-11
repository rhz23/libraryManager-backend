package br.com.rzaninelli.gestor.repository;

import br.com.rzaninelli.gestor.model.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro,Long> {

    Optional<Livro> findByIsbn(String isbn);

    Page<Livro> findByCategoria(String categoria, Pageable pageable);

    Page<Livro> findByDisponivelTrue(Pageable pageable);

    @Query("SELECT l FROM Livro l " +
            "WHERE l.categoria = :categoria " +
            "AND l.id NOT IN (SELECT e.livro.id FROM Emprestimo e " +
            "WHERE e.usuario.id = :usuarioId)")
    List<Livro> findLivrosNaoEmprestadosPorUsuarioECategoria(@Param("usuarioId") Long usuarioId, @Param("categoria") String categoria);

    @Query("SELECT l FROM Livro l WHERE l.disponivel = true ORDER BY l.titulo ASC")
    List<Livro> findAllByOrderByTituloAsc();

    @Query("SELECT l FROM Livro l WHERE l.disponivel = true ORDER BY l.dataPublicacao DESC")
    List<Livro> findAllByOrderByDataPublicacaoDesc();

    @Query("SELECT l FROM Livro l " +
            "WHERE l.categoria IN :categorias " +
            "AND l.id NOT IN (SELECT e.livro.id FROM Emprestimo e WHERE e.usuario.id = :usuarioId) " +
            "AND l.disponivel = TRUE")
    List<Livro> findLivrosPorCategoriasNaoEmprestados(@Param("usuarioId") Long usuarioId, @Param("categorias") List<String> categorias);

    @Query("SELECT l FROM Livro l " +
            "WHERE l.autor IN :autores " +
            "AND l.id NOT IN (SELECT e.livro.id FROM Emprestimo e WHERE e.usuario.id = :usuarioId) " +
            "AND l.disponivel = TRUE")
    List<Livro> findLivrosPorAutoresNaoEmprestados(@Param("usuarioId") Long usuarioId, @Param("autores") List<String> autores);

}
