package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.GestorApplication;
import br.com.rzaninelli.gestor.model.dto.request.EmprestimoRequestDTO;
import br.com.rzaninelli.gestor.model.entity.Livro;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import br.com.rzaninelli.gestor.testutils.testenviroment.CenarioUM;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GestorApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@CenarioUM
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    // ---------------------------------------------------------------
    // TESTES DE SUCESSO
    // ---------------------------------------------------------------

    @Test
    void deveCriarEmprestimoComSucesso() throws Exception {
        Usuario usuario = usuarioRepository.findAll().get(0);
        Livro livro = livroRepository.findAll().stream().filter(l -> l.getDisponivel()).findFirst().orElseThrow();

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(usuario.getId(), livro.getId());

        mockMvc.perform(post("/api/v1/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.livro.id").value(livro.getId()))
                .andExpect(jsonPath("$.usuario.id").value(usuario.getId()))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRealizarDevolucaoComSucesso() throws Exception {

        Long emprestimoId = 1L;

        mockMvc.perform(put("/api/v1/emprestimos/{id}/devolucao", emprestimoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(emprestimoId))
                .andExpect(jsonPath("$.status").value("DEVOLVIDO"));
    }

    @Test
    void deveListarEmprestimosPorUsuarioComSucesso() throws Exception {
        Usuario usuario = usuarioRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/emprestimos/usuario/{usuarioId}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].usuario.id").value(usuario.getId()));
    }


    // ---------------------------------------------------------------
    // TESTES DE ERRO
    // ---------------------------------------------------------------

    @Test
    void deveRetornarErro_QuandoLivroIndisponivel() throws Exception {
        Usuario usuario = usuarioRepository.findAll().get(0);
        Livro livroIndisponivel = livroRepository.findAll().stream()
                .filter(l -> !l.getDisponivel())
                .findFirst()
                .orElseThrow();

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(usuario.getId(), livroIndisponivel.getId());

        mockMvc.perform(post("/api/v1/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Livro não está disponível"))
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_RULE_VIOLATION")
                );
    }

    @Test
    void deveRetornarErro_QuandoEmprestimoNaoEncontradoParaDevolucao() throws Exception {
        mockMvc.perform(put("/api/v1/emprestimos/{id}/devolucao", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empréstimo com ID 999 não encontrado"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND")
                );
    }

    @Test
    void deveRetornarErro_QuandoRequestInvalido() throws Exception {
        // Campos ausentes (ambos null)
        EmprestimoRequestDTO request = new EmprestimoRequestDTO(null, null);

        mockMvc.perform(post("/api/v1/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
