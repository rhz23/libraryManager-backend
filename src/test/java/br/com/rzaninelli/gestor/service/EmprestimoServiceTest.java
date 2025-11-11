package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.GestorApplication;
import br.com.rzaninelli.gestor.enums.StatusEmprestimo;
import br.com.rzaninelli.gestor.exception.custom.BusinessException;
import br.com.rzaninelli.gestor.exception.custom.ResourceNotFoundException;
import br.com.rzaninelli.gestor.model.dto.request.EmprestimoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.EmprestimoResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Emprestimo;
import br.com.rzaninelli.gestor.model.entity.Livro;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.EmprestimoRepository;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import br.com.rzaninelli.gestor.testutils.testenviroment.CenarioUM;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = GestorApplication.class)
@ActiveProfiles("test")
@CenarioUM
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EmprestimoServiceTest {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    // ---------------------------------------------------------------
    // TESTES DE SUCESSO
    // ---------------------------------------------------------------

    @Test
    void deveRealizarEmprestimoComSucesso() {
        Usuario usuario = usuarioRepository.findAll().get(0);
        Livro livroDisponivel = livroRepository.findAll().stream()
                .filter(Livro::getDisponivel)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum livro disponível encontrado no cenário."));

        EmprestimoRequestDTO dto = new EmprestimoRequestDTO(usuario.getId(), livroDisponivel.getId());

        EmprestimoResponseDTO response = emprestimoService.realizarEmprestimo(dto);

        assertThat(response).isNotNull();
        assertThat(response.livro().id()).isEqualTo(livroDisponivel.getId());
        assertThat(response.usuario().id()).isEqualTo(usuario.getId());

        Livro livro = livroRepository.findById(livroDisponivel.getId()).orElseThrow();
        assertThat(livro.getDisponivel()).isFalse();
    }

    @Test
    void deveRealizarDevolucaoComSucesso() {
        Emprestimo emprestimoAtivo = emprestimoRepository.findAll().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO)
                .findFirst()
                .orElseThrow();

        var response = emprestimoService.realizarDevolucao(emprestimoAtivo.getId());

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(StatusEmprestimo.DEVOLVIDO);

        Livro livro = livroRepository.findById(emprestimoAtivo.getLivro().getId()).orElseThrow();
        assertThat(livro.getDisponivel()).isTrue();
    }

    // ---------------------------------------------------------------
    // TESTES DE ERRO
    // ---------------------------------------------------------------

    @Test
    void deveLancarErro_QuandoLivroNaoDisponivel() {
        Usuario usuario = usuarioRepository.findAll().get(0);
        Livro livroIndisponivel = livroRepository.findAll().stream()
                .filter(l -> !l.getDisponivel())
                .findFirst()
                .orElseThrow();

        var dto = new EmprestimoRequestDTO(usuario.getId(), livroIndisponivel.getId());

        assertThatThrownBy(() -> emprestimoService.realizarEmprestimo(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Livro não está disponível");
    }

    @Test
    void deveLancarErro_QuandoUsuarioNaoExistir() {
        Livro livroDisponivel = livroRepository.findAll().stream()
                .filter(Livro::getDisponivel)
                .findFirst()
                .orElseThrow();

        var dto = new EmprestimoRequestDTO(999L, livroDisponivel.getId());

        assertThatThrownBy(() -> emprestimoService.realizarEmprestimo(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário");
    }

    @Test
    void deveLancarErro_QuandoLivroNaoExistir() {
        Usuario usuario = usuarioRepository.findAll().get(0);

        var dto = new EmprestimoRequestDTO(usuario.getId(), 999L);

        assertThatThrownBy(() -> emprestimoService.realizarEmprestimo(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Livro");
    }

    @Test
    void deveLancarErro_QuandoDevolverEmprestimoInexistente() {
        assertThatThrownBy(() -> emprestimoService.realizarDevolucao(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Empréstimo");
    }

    @Test
    void deveLancarErro_QuandoDevolverEmprestimoNaoAtivo() {
        Emprestimo emprestimoAtivo = emprestimoRepository.findAll().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO)
                .findFirst()
                .orElseThrow();

        emprestimoService.realizarDevolucao(emprestimoAtivo.getId());

        assertThatThrownBy(() -> emprestimoService.realizarDevolucao(emprestimoAtivo.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Empréstimo não está ativo");
    }
}
