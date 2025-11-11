package br.com.rzaninelli.gestor.repository;

import br.com.rzaninelli.gestor.model.entity.Emprestimo;
import br.com.rzaninelli.gestor.enums.StatusEmprestimo;
import br.com.rzaninelli.gestor.model.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    Page<Emprestimo> findByUsuarioId(Long usuarioId, Pageable page);

    Page<Emprestimo> findByStatus(StatusEmprestimo status, Pageable page);

    Optional<Emprestimo> findByLivroIdAndStatus(Long livroId, StatusEmprestimo status);

    @Query("SELECT DISTINCT e.livro.categoria FROM Emprestimo e " +
            "WHERE e.usuario.id = :usuarioId")
    List<String> findCategoriasEmprestadasPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT DISTINCT e.livro.autor FROM Emprestimo e " +
            "WHERE e.usuario.id = :usuarioId")
    List<String> findAutoresLidosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT e.livro AS livro, COUNT(e.livro) AS totalEmprestimos " +
            "FROM Emprestimo e " +
            "WHERE e.dataEmprestimo BETWEEN :inicio AND :fim " +
            "AND e.livro.disponivel = TRUE " +
            "AND e.livro.id NOT IN (SELECT em.livro.id FROM Emprestimo em WHERE em.usuario.id = :usuarioId) " +
            "GROUP BY e.livro " +
            "ORDER BY COUNT(e.livro) DESC")
    List<Livro> findLivrosMaisEmprestados(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);


}
