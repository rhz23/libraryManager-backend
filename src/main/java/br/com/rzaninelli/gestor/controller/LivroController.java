package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.model.dto.request.LivroRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.LivroResponseDTO;
import br.com.rzaninelli.gestor.service.LivroService;
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
@RequestMapping("/api/v1/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Gerenciamento de livros")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar livro")
    public LivroResponseDTO criar(@Valid @RequestBody LivroRequestDTO dto) {
        return livroService.criar(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID")
    public LivroResponseDTO buscarPorId(@PathVariable Long id) {
        return livroService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar todos os livros")
    public Page<LivroResponseDTO> listarTodos(@PageableDefault(sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {
        return livroService.listarTodos(pageable);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar livros por categoria")
    public Page<LivroResponseDTO> listarPorCategoria(@PathVariable String categoria, @PageableDefault(sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {
        return livroService.listarPorCategoria(categoria, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar livro")
    public LivroResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LivroRequestDTO dto) {
        return livroService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar livro")
    public void deletar(@PathVariable Long id) {
        livroService.deletar(id);
    }
}
