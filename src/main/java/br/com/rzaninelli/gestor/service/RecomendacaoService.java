package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.enums.TipoRecomendacao;
import br.com.rzaninelli.gestor.mapper.LivroMapper;
import br.com.rzaninelli.gestor.model.dto.request.FiltroRecomendacaoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.LivroResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Livro;
import br.com.rzaninelli.gestor.repository.EmprestimoRepository;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecomendacaoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final LivroMapper livroMapper;

    public List<LivroResponseDTO> recomendarLivros(FiltroRecomendacaoRequestDTO  filtro) {

        // Desestrutura o DTO para usar as variáveis internamente
        Long usuarioId = filtro.usuarioId();
        TipoRecomendacao tipo = filtro.tipo();
        LocalDate inicio = filtro.inicio();
        LocalDate fim = filtro.fim();
        String sortField = filtro.sortField();
        Sort.Direction direction = filtro.direcao();

        log.info("Gerando recomendação tipo [{}] para usuário {}", tipo, usuarioId);

        List<Livro> recomendacoes = switch (tipo) {
            case CATEGORIAS -> recomendarPorCategorias(usuarioId);
            case AUTORES -> recomendarPorAutores(usuarioId);
            case LOCACOES -> recomendarPorMaisLocados(usuarioId, inicio, fim);
        };

        Comparator<Livro> comparator = getComparator(sortField, direction);
        return recomendacoes.stream()
                .sorted(comparator)
                .map(livroMapper::toResponse)
                .toList();
    }

    private List<Livro> recomendarPorCategorias(Long usuarioId) {
        List<String> categorias = emprestimoRepository.findCategoriasEmprestadasPorUsuario(usuarioId);
        if (categorias.isEmpty()) return Collections.emptyList();

        return livroRepository.findLivrosPorCategoriasNaoEmprestados(usuarioId, categorias);
    }

    private List<Livro> recomendarPorAutores(Long usuarioId) {
        List<String> autores = emprestimoRepository.findAutoresLidosPorUsuario(usuarioId);
        if (autores.isEmpty()) return Collections.emptyList();

        return livroRepository.findLivrosPorAutoresNaoEmprestados(usuarioId, autores);
    }

    private List<Livro> recomendarPorMaisLocados(Long usuarioId, LocalDate inicio, LocalDate fim) {

        LocalDateTime inicioDateTime = inicio != null ? inicio.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime fimDateTime = fim != null ? fim.atTime(23, 59, 59) : LocalDateTime.MAX;

        List<Livro> livros = emprestimoRepository.findLivrosMaisEmprestados(usuarioId, inicioDateTime, fimDateTime);
        return livros;
    }

    private Comparator<Livro> getComparator(String sortField, Sort.Direction direction) {
        Comparator<Livro> comparator = switch (sortField) {
            case "titulo" -> Comparator.comparing(Livro::getTitulo, String.CASE_INSENSITIVE_ORDER);
            case "autor" -> Comparator.comparing(Livro::getAutor, String.CASE_INSENSITIVE_ORDER);
            case "quantidadeEmprestimos" -> Comparator.comparingInt(l -> l.getEmprestimos().size());
            default -> Comparator.comparing(Livro::getTitulo);
        };

        return direction == Sort.Direction.DESC ? comparator.reversed() : comparator;
    }
}
