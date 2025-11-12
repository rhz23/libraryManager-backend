package br.com.rzaninelli.gestor.client;

import br.com.rzaninelli.gestor.exception.custom.ExternalApiException;
import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksItemDTO;
import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleBooksClient {

    private final RestTemplate restTemplate;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Value("${google.books.api.url}")
    private String apiUrl;

    @PostConstruct
    public void init() {
        log.info("Google Books Client inicializado - URL: {}", apiUrl);
    }

    /**
     * Busca livros por termo genérico
     */
    public GoogleBooksSearchResponse buscarLivros(String query, Integer maxResults) {
        try {
            String url = buildUrl(query, maxResults, 0);

            log.info("Buscando livros no Google Books: {}", query);

            ResponseEntity<GoogleBooksSearchResponse> response = restTemplate.getForEntity(
                    url,
                    GoogleBooksSearchResponse.class
            );

            GoogleBooksSearchResponse body = response.getBody();

            if (body != null && body.getItems() != null) {
                log.info("Encontrados {} livros no Google Books", body.getItems().size());
            }

            return body;

        } catch (HttpClientErrorException e) {
            log.error("Erro HTTP ao buscar no Google Books: {} - {}",
                    e.getStatusCode(), e.getMessage());
            throw new ExternalApiException("Google Books",
                    "Erro na requisição: " + e.getStatusCode());

        } catch (HttpServerErrorException e) {
            log.error("Erro no servidor do Google Books: {} - {}",
                    e.getStatusCode(), e.getMessage());
            throw new ExternalApiException("Google Books",
                    "Servidor indisponível: " + e.getStatusCode());

        } catch (ResourceAccessException e) {
            log.error("Timeout ao acessar Google Books: {}", e.getMessage());
            throw new ExternalApiException("Google Books", "Timeout na conexão");

        } catch (Exception e) {
            log.error("Erro inesperado ao buscar no Google Books", e);
            throw new ExternalApiException("Google Books",
                    "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Busca livros por ISBN
     */
    public GoogleBooksSearchResponse buscarPorIsbn(String isbn) {
        String query = "isbn:" + isbn;
        return buscarLivros(query, 1);
    }

    /**
     * Busca livros por autor
     */
    public GoogleBooksSearchResponse buscarPorAutor(String autor, Integer maxResults) {
        String query = "inauthor:" + autor;
        return buscarLivros(query, maxResults);
    }

    /**
     * Busca livros por título
     */
    public GoogleBooksSearchResponse buscarPorTitulo(String titulo, Integer maxResults) {
        String query = "intitle:\"" + titulo + "\"";
        return buscarLivros(query, maxResults);
    }

    /**
     * Busca livro específico por ID
     */
    public GoogleBooksItemDTO buscarPorId(String googleBooksId) {
        try {
            String url = String.format("%s/volumes/%s?key=%s",
                    apiUrl, googleBooksId, apiKey);

            log.info("Buscando livro por ID no Google Books: {}", googleBooksId);

            ResponseEntity<GoogleBooksItemDTO> response = restTemplate.getForEntity(
                    url,
                    GoogleBooksItemDTO.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao buscar livro por ID: {}", googleBooksId, e);
            throw new ExternalApiException("Google Books",
                    "Erro ao buscar livro por ID: " + e.getMessage());
        }
    }

    private String buildUrl(String query, Integer maxResults, Integer startIndex) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl + "/volumes")
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .queryParam("maxResults", maxResults != null ? maxResults : 10)
                .queryParam("startIndex", startIndex != null ? startIndex : 0)
                .encode()
                .toUriString();
    }
}
