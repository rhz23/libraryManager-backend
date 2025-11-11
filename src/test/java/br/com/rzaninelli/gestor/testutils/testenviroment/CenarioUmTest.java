package br.com.rzaninelli.gestor.testutils.testenviroment;

import br.com.rzaninelli.gestor.enums.StatusEmprestimo;
import br.com.rzaninelli.gestor.model.entity.*;
import br.com.rzaninelli.gestor.repository.*;
import br.com.rzaninelli.gestor.testutils.TempoTestUtil;
import org.junit.jupiter.api.extension.*;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
public class CenarioUmTest implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext appContext = SpringExtension.getApplicationContext(context);

        UsuarioRepository usuarioRepository = appContext.getBean(UsuarioRepository.class);
        LivroRepository livroRepository = appContext.getBean(LivroRepository.class);
        EmprestimoRepository emprestimoRepository = appContext.getBean(EmprestimoRepository.class);

        // Define o tempo base fixo
        TempoTestUtil.fixarAgora(LocalDateTime.of(2025, 1, 10, 10, 0));

        // Limpa dados
        emprestimoRepository.deleteAll();
        livroRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria um usuário
        Usuario usuario = new Usuario();
        usuario.setNome("João da Silva");
        usuario.setEmail("joao@teste.com");
        usuario.setTelefone("11999999999");
        usuario.setAtivo(true);
        usuario.setDataCadastro(LocalDate.now());
        usuarioRepository.save(usuario);

        // Cria livros
        Livro hobbit = new Livro(null, "O Hobbit", "J.R.R. Tolkien", "1234567890123",
                LocalDate.of(1937, 9, 21), "Fantasia", true, new ArrayList<>());

        Livro distopia = new Livro(null, "1984", "George Orwell", "9876543210987",
                LocalDate.of(1949, 6, 8), "Distopia", true, new ArrayList<>());

        livroRepository.save(hobbit);
        livroRepository.save(distopia);

        // Cria um empréstimo ativo de 2 dias atrás
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(hobbit);
        emprestimo.setDataEmprestimo(TempoTestUtil.agora().minusDays(2));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimoRepository.save(emprestimo);

        // Atualiza disponibilidade
        hobbit.setDisponivel(false);
        livroRepository.save(hobbit);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        TempoTestUtil.restaurarAgora();
    }
}
