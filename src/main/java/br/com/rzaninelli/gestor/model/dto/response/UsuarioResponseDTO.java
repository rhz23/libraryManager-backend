package br.com.rzaninelli.gestor.model.dto.response;

import java.time.LocalDateTime;

public record UsuarioResponseDTO (Long id, String nome, String email, String telefone, LocalDateTime dataCadastro, Boolean ativo) {}
