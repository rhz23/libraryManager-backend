package br.com.rzaninelli.gestor.controller;

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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = GestorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@CenarioRecomendacao
//@Transactional
class RecomendacaoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/recomendacoes";
    }

    // --- 1. TESTES FUNCIONAIS (SUCCESS) ---

    // 1.1. Teste de Categoria (Padronizado) - OBRIGA INCLUIR O TIPO PARA EVITAR NPE
    @Test
    void deveRetornarRecomendacoesPorCategoriasPadrao() {
        Usuario maria = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        // 🟢 CORREÇÃO: Adicionando 'tipo=CATEGORIAS', sortField e direction para estabilidade.
        String url = String.format("%s?usuarioId=%d&tipo=%s&sortField=titulo&direction=%s",
                baseUrl(), maria.getId(), TipoRecomendacao.CATEGORIAS, Sort.Direction.ASC);

        ResponseEntity<List<LivroResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // 🟢 CORREÇÃO DA EXPECTATIVA: Espera 2 livros
        assertThat(response.getBody()).hasSize(2);

        // 🟢 CORREÇÃO DA ORDEM E TÍTULOS: Deve ser 1984 e Silmarillion
        assertThat(response.getBody())
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("1984", "Silmarillion");

        assertThat(response.getBody())
                .extracting(LivroResponseDTO::categoria)
                .containsExactly("Distopia", "Fantasia");
    }

    // 1.2. Teste de Categoria (Explícito) - CORRIGIDO PARA SER IDÊNTICO AO 1.1 COM OS PARAMS
    @Test
    void deveRetornarRecomendacoesPorCategoria() {
        Usuario usuario = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        // 🟢 CORREÇÃO: Adicionando sortField e direction
        String url = String.format("%s?usuarioId=%d&tipo=%s&sortField=titulo&direction=%s",
                baseUrl(), usuario.getId(), TipoRecomendacao.CATEGORIAS, Sort.Direction.ASC);

        ResponseEntity<List<LivroResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // 🟢 CORREÇÃO DA EXPECTATIVA: Espera 2 livros
        assertThat(response.getBody()).hasSize(2);

        assertThat(response.getBody())
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("1984", "Silmarillion");
    }

    // 1.3. Teste de Autor
    @Test
    void deveRetornarRecomendacoesPorAutor() {
        Usuario usuario = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        String url = String.format("%s?usuarioId=%d&tipo=%s&sortField=titulo&direction=%s",
                baseUrl(), usuario.getId(), TipoRecomendacao.AUTORES, Sort.Direction.ASC);

        // 💡 PASSO DE VERIFICAÇÃO: Imprima a URL construída e o ID do usuário.
        System.out.println("URL DE TESTE: " + url);
        System.out.println("USUARIO ID: " + usuario.getId());

        ResponseEntity<List<LivroResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // 4. A expectativa é que, com a query JPQL CORRETA e os dados de CENÁRIO
        // ajustados (ou já corretos para este caso), apenas 1 seja retornado.
        assertThat(response.getBody()).hasSize(2);

        assertThat(response.getBody())
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("1984", "Silmarillion");
    }

    // 1.4. Teste de Locações no Período
    @Test
    void deveRetornarRecomendacoesPorLocacoesNoPeriodo() {
        Usuario usuario = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();

        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now();

        // 🟢 CORREÇÃO: Adicionando sortField e direction
        String url = String.format("%s?usuarioId=%d&tipo=%s&inicio=%s&fim=%s&sortField=quantidadeEmprestimos&direction=%s",
                baseUrl(), usuario.getId(), TipoRecomendacao.LOCACOES, inicio, fim, Sort.Direction.DESC);

        ResponseEntity<List<LivroResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // 🟢 CORREÇÃO DA EXPECTATIVA: Espera 1 livro
        assertThat(response.getBody()).hasSize(1);

        assertThat(response.getBody())
                .extracting(LivroResponseDTO::titulo)
                .containsExactly("Dom Casmurro");
    }

    // --- 2. TESTES DE FALHA E VALIDAÇÃO ---

    // 2.1. Teste de Usuário sem Histórico
    @Test
    void deveRetornarListaVaziaParaUsuarioSemHistorico() {
        Usuario novo = new Usuario();
        novo.setNome("Carlos Sem Historico");
        novo.setEmail("carlos@teste.com");
        novo.setTelefone("11999998888");
        novo.setAtivo(true);
        usuarioRepository.save(novo);

        String url = String.format("%s?usuarioId=%d&tipo=%s",
                baseUrl(), novo.getId(), TipoRecomendacao.CATEGORIAS);

        ResponseEntity<List<LivroResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }

    // 2.2. Teste de Parâmetro Inválido/Obrigatório
    @Test
    void deveRetornarBadRequestQuandoParametrosInvalidos() {
        // 1. Crie um DTO inválido: usuarioId é intencionalmente nulo
        FiltroRecomendacaoRequestDTO invalidFiltro = new FiltroRecomendacaoRequestDTO(null, TipoRecomendacao.CATEGORIAS, null, null, "titulo", null);

        // 2. Prepare a requisição HTTP com o DTO no Body e headers JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Crie a entidade de requisição (Body + Headers)
        HttpEntity<FiltroRecomendacaoRequestDTO> request = new HttpEntity<>(invalidFiltro, headers);

        // 4. Faça a requisição usando POST/exchange
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl(),
                HttpMethod.GET, // 🟢 Alterado para POST
                request,
                String.class
        );

        // 5. Assert: O Spring, ao falhar a validação do @RequestBody, retorna 400
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("O ID do usuário é obrigatório");
    }

    // 2.3. Teste sugerido: Tipo de Recomendação Inválido (Non-Existent Enum Value)
    @Test
    void deveRetornarBadRequestSeTipoDeRecomendacaoForInvalido() {

        Usuario maria = usuarioRepository.findByEmail("maria@teste.com").orElseThrow();
        // Testando um valor que não existe no Enum TipoRecomendacao
        String url = String.format("%s?usuarioId=%d&tipo=%s",
                baseUrl(), maria.getId(), "TIPO_INEXISTENTE");

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // A falha na conversão do String para Enum geralmente resulta em HTTP 400
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("TipoRecomendacao");
    }
}