package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.GestorApplication;
import br.com.rzaninelli.gestor.exception.custom.DuplicateResourceException;
import br.com.rzaninelli.gestor.exception.custom.ResourceNotFoundException;
import br.com.rzaninelli.gestor.mapper.UsuarioMapper;
import br.com.rzaninelli.gestor.model.dto.request.UsuarioRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.UsuarioResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import br.com.rzaninelli.gestor.testutils.testenviroment.CenarioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(classes = GestorApplication.class)
@ActiveProfiles("test")
@CenarioUsuario
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UsuarioServiceTest {

    // Injetando as dependências reais (Service, Repository, Mapper)
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper; // Mapeador real

    private UsuarioRequestDTO requestDTO;
    private Usuario usuarioPreExistente;
    private final String EMAIL_EXISTENTE = "existente@teste.com";

    @BeforeEach
    void setUp() {
        // Dados para a requisição de criação
        requestDTO = new UsuarioRequestDTO("Novo Usuário Teste", "novo@teste.com", "999999999");

        // Cria e salva um usuário diretamente no banco para cenários de busca/duplicidade
        usuarioPreExistente = Usuario.builder()
                .nome("Usuario Pre Existente")
                .email(EMAIL_EXISTENTE)
                .telefone("111111111")
                .dataCadastro(LocalDate.now())
                .ativo(true)
                .build();
        usuarioPreExistente = usuarioRepository.save(usuarioPreExistente);
    }

    // --- Testes para CRIAR ---

    @Test
    @DisplayName("Deve criar e persistir um novo usuário com sucesso")
    void deveCriarEPersistirUsuarioComSucesso() {

        // Act
        var result = usuarioService.criar(requestDTO);

        // Assert
        // 1. Verifica o DTO de retorno
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(requestDTO.email());

        // 2. Verifica o estado do banco de dados (confirma a persistência)
        Optional<Usuario> persisted = usuarioRepository.findByEmail(requestDTO.email());
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getNome()).isEqualTo(requestDTO.nome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao tentar criar usuário com email duplicado")
    void deveLancarDuplicateResourceException() {
        // Arrange
        UsuarioRequestDTO duplicadoDTO = new UsuarioRequestDTO("Duplicado", EMAIL_EXISTENTE, "000000000");

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criar(duplicadoDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Usuario com 'email' já existe");
    }

    // --- Testes para BUSCAR POR ID ---

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange - Usuário já existe no BeforeEach
        Long id = usuarioPreExistente.getId();

        // Act
        var result = usuarioService.buscarPorId(id);

        // Assert
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo(EMAIL_EXISTENTE);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar por ID inexistente")
    void deveLancarResourceNotFoundExceptionAoBuscarPorId() {
        // Arrange
        Long idInexistente = 999L;

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.buscarPorId(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário com ID 999 não encontrado");
    }

    // --- Testes para BUSCAR TODOS ---

    @Test
    @DisplayName("Deve retornar página de usuários com sucesso e ordenado")
    void deveBuscarTodosComPaginacaoEOrdenacao() {
        // Arrange
        Usuario maria = Usuario.builder().nome("Maria ZZZ").email("maria@teste.com").telefone("333333333").ativo(true).build();
        usuarioRepository.save(maria);
        Usuario ana = Usuario.builder().nome("Ana AAA").email("ana@teste.com").telefone("444444444").ativo(true).build();
        usuarioRepository.save(ana);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());

        // Act
        Page<UsuarioResponseDTO> resultPage = usuarioService.buscarTodos(pageable);

        // Assert
        assertThat(resultPage.getTotalElements()).isEqualTo(6);
        assertThat(resultPage.getContent().get(0).nome()).isEqualTo("Ana AAA");
        assertThat(resultPage.getContent().get(1).nome()).isEqualTo("Maria ZZZ"); // Corrigido
        assertThat(resultPage.getContent().get(2).nome()).isEqualTo("Usuario Pre Existente"); // Corrigido
    }

    // --- Testes para ATUALIZAR ---

    @Test
    @DisplayName("Deve atualizar o nome e telefone de um usuário e persistir as mudanças")
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        Long id = usuarioPreExistente.getId();
        UsuarioRequestDTO updateDTO = new UsuarioRequestDTO("Nome Atualizado", null, "888888888");

        // Act
        var result = usuarioService.atualizar(id, updateDTO);

        // Assert
        assertThat(result.nome()).isEqualTo("Nome Atualizado");
        assertThat(result.telefone()).isEqualTo("888888888");
        assertThat(result.email()).isEqualTo(EMAIL_EXISTENTE);

        // Confirma a atualização no banco (o save deve ter ocorrido)
        Usuario usuarioAtualizado = usuarioRepository.findById(id).orElseThrow();
        assertThat(usuarioAtualizado.getNome()).isEqualTo("Nome Atualizado");
        assertThat(usuarioAtualizado.getTelefone()).isEqualTo("888888888");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar ID inexistente")
    void deveLancarResourceNotFoundExceptionAoAtualizar() {
        // Arrange
        Long idInexistente = 999L;

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizar(idInexistente, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- Testes para DELETAR (Inativar) ---

    @Test
    @DisplayName("Deve inativar (deletar lógico) um usuário com sucesso")
    void deveInativarUsuarioComSucesso() {
        // Arrange
        Long id = usuarioPreExistente.getId();

        // Act
        usuarioService.deletar(id);

        // Assert
        // Verifica se o flag 'ativo' foi atualizado no banco
        Usuario usuarioInativado = usuarioRepository.findById(id).orElseThrow();
        assertThat(usuarioInativado.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar ID inexistente")
    void deveLancarResourceNotFoundExceptionAoDeletar() {
        // Arrange
        Long idInexistente = 999L;

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.deletar(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
