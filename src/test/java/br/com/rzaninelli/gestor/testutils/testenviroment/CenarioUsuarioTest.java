package br.com.rzaninelli.gestor.testutils.testenviroment;

import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
public class CenarioUsuarioTest implements BeforeEachCallback, AfterEachCallback {

    public static final String EMAIL_JOAO = "joao.teste@email.com";
    public static final String EMAIL_MARIA = "maria.teste@email.com";
    public static final String EMAIL_PEDRO = "pedro.teste@email.com";

    private UsuarioRepository usuarioRepository;

    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext appContext = SpringExtension.getApplicationContext(context);
        usuarioRepository = appContext.getBean(UsuarioRepository.class);

        usuarioRepository.deleteAllInBatch();

        Usuario joao = Usuario.builder()
                .nome("João Teste Ativo")
                .email(EMAIL_JOAO)
                .telefone("11911111111")
                .dataCadastro(LocalDate.now())
                .ativo(true)
                .build();
        usuarioRepository.save(joao);

        Usuario maria = Usuario.builder()
                .nome("Maria Teste Ativa")
                .email(EMAIL_MARIA)
                .telefone("11922222222")
                .dataCadastro(LocalDate.now())
                .ativo(true)
                .build();
        usuarioRepository.save(maria);

        Usuario pedro = Usuario.builder()
                .nome("Pedro Teste Inativo")
                .email(EMAIL_PEDRO)
                .telefone("11933333333")
                .dataCadastro(LocalDate.now())
                .ativo(false)
                .build();
        usuarioRepository.save(pedro);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (usuarioRepository != null) {
            usuarioRepository.deleteAllInBatch();
        }
    }
}
