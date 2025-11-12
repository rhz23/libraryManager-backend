package br.com.rzaninelli.gestor.controller;

import br.com.rzaninelli.gestor.client.GoogleBooksService;
import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/google-books")
@RequiredArgsConstructor
@Tag(name = "Google Books", description = "Integração com Google Books API")
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;

    @GetMapping("/buscar/titulo")
    @Operation(summary = "Buscar livros por título")
    public ResponseEntity<List<GoogleBooksResponseDTO>> buscarPorTitulo(
            @RequestParam String titulo,
            @RequestParam(defaultValue = "10") Integer maxResults) {

        List<GoogleBooksResponseDTO> livros =
                googleBooksService.buscarPorTitulo(titulo, maxResults);

        return ResponseEntity.ok(livros);
    }

    @GetMapping("/buscar/autor")
    @Operation(summary = "Buscar livros por autor")
    public ResponseEntity<List<GoogleBooksResponseDTO>> buscarPorAutor(
            @RequestParam String autor,
            @RequestParam(defaultValue = "10") Integer maxResults) {

        List<GoogleBooksResponseDTO> livros =
                googleBooksService.buscarPorAutor(autor, maxResults);

        return ResponseEntity.ok(livros);
    }

    @GetMapping("/buscar/isbn/{isbn}")
    @Operation(summary = "Buscar livro por ISBN")
    public ResponseEntity<GoogleBooksResponseDTO> buscarPorIsbn(@PathVariable String isbn) {
        return googleBooksService.buscarPorIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
