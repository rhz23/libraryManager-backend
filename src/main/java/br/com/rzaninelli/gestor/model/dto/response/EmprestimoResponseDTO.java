package br.com.rzaninelli.gestor.model.dto.response;

import br.com.rzaninelli.gestor.enums.StatusEmprestimo;

import java.time.LocalDateTime;

public record EmprestimoResponseDTO (Long id, UsuarioResponseDTO usuario, LivroResponseDTO livro,
                                     LocalDateTime dataEmprestimo, LocalDateTime dataDevolucao, StatusEmprestimo status) {
}
