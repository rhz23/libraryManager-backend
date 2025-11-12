package br.com.rzaninelli.gestor.client;

import br.com.rzaninelli.gestor.exception.custom.ExternalApiException;
import br.com.rzaninelli.gestor.mapper.GoogleBooksMapper;
import br.com.rzaninelli.gestor.mapper.LivroMapper;
import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksResponseDTO;
import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksSearchResponse;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GoogleBooksService {

    private final GoogleBooksClient googleBooksClient;
    private final LivroRepository livroRepository;
    private final GoogleBooksMapper googleBooksMapper;
    private final LivroMapper livroMapper;

    /**
     * Busca livros por título
     */
    public List<GoogleBooksResponseDTO> buscarPorTitulo(String titulo, Integer maxResults) {
        log.info("Buscando livros por título: {}", titulo);

        try {
            GoogleBooksSearchResponse response =
                    googleBooksClient.buscarPorTitulo(titulo, maxResults);

            if (response == null || response.getItems() == null) {
                return Collections.emptyList();
            }

            return googleBooksMapper.toDTOList(response.getItems());

        } catch (ExternalApiException e) {
            log.error("Erro ao buscar livros por título", e);
            throw e;
        }
    }

    /**
     * Busca livros por autor
     */
    public List<GoogleBooksResponseDTO> buscarPorAutor(String autor, Integer maxResults) {
        log.info("Buscando livros por autor: {}", autor);

        GoogleBooksSearchResponse response =
                googleBooksClient.buscarPorAutor(autor, maxResults);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        return googleBooksMapper.toDTOList(response.getItems());
    }

    /**
     * Busca livros por ISBN
     */
    public Optional<GoogleBooksResponseDTO> buscarPorIsbn(String isbn) {
        log.info("Buscando livro por ISBN: {}", isbn);

        GoogleBooksSearchResponse response = googleBooksClient.buscarPorIsbn(isbn);

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(googleBooksMapper.toDTO(response.getItems().get(0)));
    }
}
