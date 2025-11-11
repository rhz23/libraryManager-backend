package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.model.dto.request.EmprestimoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.EmprestimoResponseDTO;
import br.com.rzaninelli.gestor.service.EmprestimoService;
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
@RequestMapping("/api/v1/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Gerenciamento de empréstimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realizar empréstimo")
    public EmprestimoResponseDTO realizarEmprestimo(
            @Valid @RequestBody EmprestimoRequestDTO dto) {
        return emprestimoService.realizarEmprestimo(dto);
    }

    @PutMapping("/{id}/devolucao")
    @Operation(summary = "Realizar devolução")
    public EmprestimoResponseDTO realizarDevolucao(@PathVariable Long id) {
        return emprestimoService.realizarDevolucao(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar empréstimos por usuário")
    public Page<EmprestimoResponseDTO> listarPorUsuario(@PathVariable Long usuarioId, @PageableDefault(sort = "usuario", direction = Sort.Direction.ASC) Pageable pageable) {
        return emprestimoService.listarPorUsuario(usuarioId, pageable);
    }
}
