package br.com.rzaninelli.gestor.testutils.testenviroment;

import br.com.rzaninelli.gestor.enums.StatusEmprestimo;
import br.com.rzaninelli.gestor.model.entity.Emprestimo;
import br.com.rzaninelli.gestor.model.entity.Livro;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.EmprestimoRepository;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import br.com.rzaninelli.gestor.testutils.TempoTestUtil;
import org.junit.jupiter.api.extension.*;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CenarioRecomendacaoTest implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext appContext = SpringExtension.getApplicationContext(context);

        UsuarioRepository usuarioRepository = appContext.getBean(UsuarioRepository.class);
        LivroRepository livroRepository = appContext.getBean(LivroRepository.class);
        EmprestimoRepository emprestimoRepository = appContext.getBean(EmprestimoRepository.class);

        new CenarioUmTest().beforeEach(context);

        TempoTestUtil.fixarAgora(LocalDateTime.of(2025, 2, 10, 10, 0));

        Usuario maria = new Usuario();
        maria.setNome("Maria Oliveira");
        maria.setEmail("maria@teste.com");
        maria.setTelefone("11988888888");
        maria.setAtivo(true);
        maria.setDataCadastro(LocalDate.now());
        usuarioRepository.save(maria);

        //Add livros disponiveis
        List<Livro> livros = List.of(
                new Livro(null, "O Senhor dos Anéis", "J.R.R. Tolkien", "1111111111111",
                        LocalDate.of(1954, 7, 29), "Fantasia", true, new ArrayList<>()),

                new Livro(null, "A Revolução dos Bichos", "George Orwell", "2222222222222",
                        LocalDate.of(1945, 8, 17), "Distopia", true, new ArrayList<>()),

                new Livro(null, "Dom Casmurro", "Machado de Assis", "3333333333333",
                        LocalDate.of(1899, 1, 1), "Clássico", true, new ArrayList<>()),

                new Livro(null, "Silmarillion", "J.R.R. Tolkien", "4444444444444",
                        LocalDate.of(1977, 9, 15), "Fantasia", true, new ArrayList<>()), // mesmo autor/categoria (recomendado 1)

                new Livro(null, "1985", "Anthony Burgess", "5555555555555",
                        LocalDate.of(1962, 1, 1), "Distopia", false, new ArrayList<>())   // mesma categoria, mas autor diferente do que Maria leu
        );

        livroRepository.saveAll(livros);

        //Empréstimos de Maria
        Emprestimo emp1 = new Emprestimo();
        emp1.setUsuario(maria);
        emp1.setLivro(livros.get(0));
        emp1.setDataEmprestimo(LocalDateTime.now().minusDays(10));
        emp1.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimoRepository.save(emp1);

        Emprestimo emp2 = new Emprestimo();
        emp2.setUsuario(maria);
        emp2.setLivro(livros.get(1));
        emp2.setDataEmprestimo(LocalDateTime.now().minusDays(5));
        emp2.setStatus(StatusEmprestimo.ATIVO);
        emprestimoRepository.save(emp2);

        Livro livroIndisponivel = livros.get(1);
        livroIndisponivel.setDisponivel(false);
        livroRepository.save(livroIndisponivel);

        //Emprestimos de João
        Usuario joao = usuarioRepository.findByEmail("joao@teste.com").orElseThrow();

        Livro livroPopular = livros.get(2);
        for (int i = 0; i < 3; i++) {
            Emprestimo emp = new Emprestimo();
            emp.setUsuario(joao);
            emp.setLivro(livroPopular);
            emp.setDataEmprestimo(LocalDateTime.now().minusDays(3 + i));
            emp.setStatus(StatusEmprestimo.DEVOLVIDO);
            emprestimoRepository.save(emp);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        TempoTestUtil.restaurarAgora();
    }
}
