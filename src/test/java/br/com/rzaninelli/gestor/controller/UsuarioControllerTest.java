package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.GestorApplication;
import br.com.rzaninelli.gestor.model.dto.request.UsuarioRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.UsuarioResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import br.com.rzaninelli.gestor.testutils.testenviroment.CenarioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = GestorApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@CenarioUsuario
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UsuarioControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/usuarios";
    }

    private Usuario usuarioJoao;
    private Usuario usuarioMaria;

    @BeforeEach
    void setupCenario() {
        usuarioRepository.deleteAllInBatch();

        usuarioJoao = Usuario.builder()
                .nome("João Teste")
                .email("joao.teste@email.com")
                .telefone("111111111")
                .dataCadastro(LocalDate.now())
                .ativo(true)
                .build();
        usuarioJoao = usuarioRepository.save(usuarioJoao);

        usuarioMaria = Usuario.builder()
                .nome("Maria Teste")
                .email("maria.teste@email.com")
                .telefone("222222222")
                .dataCadastro(LocalDate.now())
                .ativo(true)
                .build();
        usuarioMaria = usuarioRepository.save(usuarioMaria);
    }

    // --- Testes para POST /api/v1/usuarios (Criar) ---

    @Test
    @DisplayName("Deve criar um usuário e retornar status 201 CREATED")
    void deveCriarUsuarioERetornar201() {
        // Arrange
        UsuarioRequestDTO novoUsuario = new UsuarioRequestDTO("Pedro Teste", "pedro.teste@email.com", "333333333");

        // Act
        ResponseEntity<UsuarioResponseDTO> response = restTemplate.postForEntity(
                baseUrl(), novoUsuario, UsuarioResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().nome()).isEqualTo("Pedro Teste");
        assertThat(response.getBody().id()).isNotNull();

        // Limpeza (opcional, mas bom para garantir)
        usuarioRepository.deleteById(response.getBody().id());
    }

    @Test
    @DisplayName("Deve retornar status 400 BAD REQUEST ao criar usuário com email duplicado")
    void deveRetornar400AoCriarComEmailDuplicado() {
        // Arrange
        // Usando o email do usuário já criado (usuarioJoao)
        UsuarioRequestDTO duplicado = new UsuarioRequestDTO("Novo João", usuarioJoao.getEmail(), "999999999");

        // Act
        ResponseEntity<Object> response = restTemplate.postForEntity(
                baseUrl(), duplicado, Object.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT); // Esperado CONFLICT (409) pela DuplicateResourceException
    }

    @Test
    @DisplayName("Deve retornar status 400 BAD REQUEST ao criar usuário com campos inválidos (Nome nulo)")
    void deveRetornar400AoCriarComNomeNulo() {
        // Arrange
        UsuarioRequestDTO invalido = new UsuarioRequestDTO(null, "invalido@email.com", "444444444");

        // Act
        ResponseEntity<Object> response = restTemplate.postForEntity(
                baseUrl(), invalido, Object.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // --- Testes para GET /api/v1/usuarios/{id} (Buscar por ID) ---

    @Test
    @DisplayName("Deve buscar usuário por ID e retornar status 200 OK")
    void deveBuscarUsuarioPorIdERetornar200() {
        // Arrange - Usuário João já está no banco
        Long id = usuarioJoao.getId();

        // Act
        ResponseEntity<UsuarioResponseDTO> response = restTemplate.getForEntity(
                baseUrl() + "/{id}", UsuarioResponseDTO.class, id);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
        assertThat(response.getBody().email()).isEqualTo(usuarioJoao.getEmail());
    }

    @Test
    @DisplayName("Deve retornar status 404 NOT FOUND ao buscar por ID inexistente")
    void deveRetornar404AoBuscarPorIdInexistente() {
        // Arrange
        Long idInexistente = 999L;

        // Act
        ResponseEntity<UsuarioResponseDTO> response = restTemplate.getForEntity(
                baseUrl() + "/{id}", UsuarioResponseDTO.class, idInexistente);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- Testes para GET /api/v1/usuarios (Listar todos) ---

    @Test
    @DisplayName("Deve listar todos os usuários com paginação e retornar status 200 OK")
    void deveListarTodosComPaginacaoERetornar200() {
        // Arrange - Usuários João e Maria já estão no banco

        // Act
        ResponseEntity<Page<UsuarioResponseDTO>> response = restTemplate.exchange(
                baseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // A lista deve conter os 2 usuários criados no setup
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(2);
        // A ordenação padrão é por "nome" ASC ("João Teste", "Maria Teste")
        assertThat(response.getBody().getContent().stream().map(UsuarioResponseDTO::nome).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("João Teste", "Maria Teste");
    }

    // --- Testes para PUT /api/v1/usuarios/{id} (Atualizar) ---

    @Test
    @DisplayName("Deve atualizar um usuário e retornar status 200 OK")
    void deveAtualizarUsuarioERetornar200() {
        // Arrange
        Long id = usuarioJoao.getId();
        UsuarioRequestDTO updateDTO = new UsuarioRequestDTO("Novo Nome do João", null, "000000000");

        // Act
        ResponseEntity<UsuarioResponseDTO> response = restTemplate.exchange(
                baseUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updateDTO),
                UsuarioResponseDTO.class,
                id);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().nome()).isEqualTo("Novo Nome do João");
        assertThat(response.getBody().telefone()).isEqualTo("000000000");
        assertThat(response.getBody().email()).isEqualTo(usuarioJoao.getEmail()); // Email deve permanecer o mesmo
    }

    @Test
    @DisplayName("Deve retornar status 404 NOT FOUND ao tentar atualizar ID inexistente")
    void deveRetornar404AoAtualizarIdInexistente() {
        // Arrange
        Long idInexistente = 999L;
        UsuarioRequestDTO updateDTO = new UsuarioRequestDTO("Novo Nome", null, "000000000");

        // Act
        ResponseEntity<UsuarioResponseDTO> response = restTemplate.exchange(
                baseUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updateDTO),
                UsuarioResponseDTO.class,
                idInexistente);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- Testes para DELETE /api/v1/usuarios/{id} (Deletar/Inativar) ---

    @Test
    @DisplayName("Deve inativar um usuário e retornar status 204 NO CONTENT")
    void deveInativarUsuarioERetornar204() {
        // Arrange
        Long id = usuarioMaria.getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verifica se o usuário foi inativado no banco
        Usuario usuarioInativado = usuarioRepository.findById(id).orElseThrow();
        assertThat(usuarioInativado.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar status 404 NOT FOUND ao tentar deletar ID inexistente")
    void deveRetornar404AoDeletarIdInexistente() {
        // Arrange
        Long idInexistente = 999L;

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                idInexistente);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
