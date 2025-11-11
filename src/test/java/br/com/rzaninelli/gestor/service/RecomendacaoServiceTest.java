package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.GestorApplication;
import br.com.rzaninelli.gestor.enums.TipoRecomendacao;
import br.com.rzaninelli.gestor.model.dto.request.FiltroRecomendacaoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.LivroResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import br.com.rzaninelli.gestor.testutils.testenviroment.CenarioRecomendacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = GestorApplication.class)
@ActiveProfiles("test")
@CenarioRecomendacao
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RecomendacaoServiceTest {

    @Autowired
    private RecomendacaoService recomendacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRecomendarPorCategoriasComBaseEmHistoricoDoUsuario() {
        Usuario usuario = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        FiltroRecomendacaoRequestDTO request = new FiltroRecomendacaoRequestDTO(usuario.getId(), TipoRecomendacao.CATEGORIAS, null, null, "titulo", Sort.Direction.ASC);

        List<LivroResponseDTO> recomendacoes = recomendacaoService.recomendarLivros(request);

        assertThat(recomendacoes)
                .as("Deve recomendar os dois livros da categoria Fantasia e Distopia ainda não lidos")
                .hasSize(2)
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("1984", "Silmarillion"); // Ordenado por título ASC
    }

    @Test
    void deveRecomendarPorAutorComBaseEmHistoricoDoUsuario() {
        Usuario usuario = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        FiltroRecomendacaoRequestDTO request = new FiltroRecomendacaoRequestDTO(usuario.getId(), TipoRecomendacao.AUTORES, null, null, "titulos", Sort.Direction.ASC);

        List<LivroResponseDTO> recomendacoes = recomendacaoService.recomendarLivros(request);

        assertThat(recomendacoes)
                .as("Deve recomendar Silmarillion (mesmo autor de O Senhor dos Anéis) e excluir o lido/indisponível")
                .hasSize(2)
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("1984", "Silmarillion");
    }

    @Test
    void deveRecomendarMaisLocadosPorPeriodoEOrdenarPorQuantidade() {

        Usuario usuario = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now();

        FiltroRecomendacaoRequestDTO request = new FiltroRecomendacaoRequestDTO(usuario.getId(), TipoRecomendacao.LOCACOES, inicio, fim, "quantidadeEmprestimos", Sort.Direction.DESC);

        List<LivroResponseDTO> recomendacoes = recomendacaoService.recomendarLivros(request);

        assertThat(recomendacoes)
                .as("Deve retornar apenas 'Dom Casmurro', pois é o único livro mais locado que Maria não leu e que está disponível.")
                .hasSize(1)
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("Dom Casmurro");
    }
}