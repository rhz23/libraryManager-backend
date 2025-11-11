package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.model.dto.request.UsuarioRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.UsuarioResponseDTO;
import br.com.rzaninelli.gestor.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário")
    public UsuarioResponseDTO criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.criar(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário", description = "Busca usuário por ID")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários")
    public Page<UsuarioResponseDTO> listarTodos(@PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        return usuarioService.buscarTodos(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados do usuário")
    public UsuarioResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar usuário", description = "Remove um usuário")
    public void deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
    }

    //TODO - implementar buscar usuarios por filtros

}
