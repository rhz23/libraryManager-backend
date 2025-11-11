package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.model.dto.request.FiltroRecomendacaoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.LivroResponseDTO;
import br.com.rzaninelli.gestor.service.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recomendacoes")
@RequiredArgsConstructor
@Tag(name = "Recomendações", description = "Recomendações de livros")
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    @GetMapping
    @Operation(summary = "Gerar recomendações de livros")
    public List<LivroResponseDTO> recomendar(@Valid @ModelAttribute FiltroRecomendacaoRequestDTO filtro) {

        return recomendacaoService.recomendarLivros(filtro);
    }
}

