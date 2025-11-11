package br.com.rzaninelli.gestor.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca API")
                        .version("1.0")
                        .description("API para gestão de biblioteca com recomendações")
                        .contact(new Contact()
                                .name("Seu Nome")
                                .email("seu@email.com"))
                )
                        .externalDocs(new ExternalDocumentation()
                                .description("Sobre o Projeto")
                                .url("https://biblioteca.com/docs")
                );
    }

}
