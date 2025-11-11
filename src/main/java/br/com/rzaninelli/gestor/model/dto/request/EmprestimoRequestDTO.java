package br.com.rzaninelli.gestor.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequestDTO (@NotNull Long usuarioId, @NotNull Long livroId) {
}
