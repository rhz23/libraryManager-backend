package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.exception.custom.DatabaseException;
import br.com.rzaninelli.gestor.exception.custom.DuplicateResourceException;
import br.com.rzaninelli.gestor.exception.custom.ResourceNotFoundException;
import br.com.rzaninelli.gestor.mapper.LivroMapper;
import br.com.rzaninelli.gestor.model.dto.request.LivroRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.LivroResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Livro;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import br.com.rzaninelli.gestor.utils.UpdateUtil;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroMapper livroMapper;

    @Transactional
    public LivroResponseDTO criar(LivroRequestDTO dto) {

        String isbnNormalizado = dto.isbn().replaceAll("[^0-9Xx]", "");

        log.info("Criando livro com ISBN: {}", isbnNormalizado);

        livroRepository.findByIsbn(isbnNormalizado).ifPresent(livro -> {
            throw new DuplicateResourceException("Livro", "isbn", isbnNormalizado);
        });

        try {
            Livro livro = livroMapper.toEntity(dto);
            livro.setIsbn(isbnNormalizado);
            livro = livroRepository.save(livro);
            log.info("Livro criado com sucesso. ISBN: {}", livro.getIsbn());
            return livroMapper.toResponse(livro);
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            log.error("Erro ao criar livro", ex);
            throw new DatabaseException("Erro ao persistir livro no banco de dados", ex);
        }
    }

    public LivroResponseDTO buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro", id));
        return livroMapper.toResponse(livro);
    }

    public Page<LivroResponseDTO> listarTodos( Pageable pageable) {

        return livroRepository.findAll(pageable)
                .map(livroMapper::toResponse);
    }

    public Page<LivroResponseDTO> listarPorCategoria(String categoria, Pageable pageable) {
        return livroRepository.findByCategoria(categoria, pageable)
                .map(livroMapper::toResponse);
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro", id));

        UpdateUtil.updateIfNotNullAndIfChanged(livro::getTitulo, livro::setTitulo, dto.titulo());
        UpdateUtil.updateIfNotNullAndIfChanged(livro::getAutor, livro::setAutor, dto.autor());
        UpdateUtil.updateIfNotNullAndIfChanged(livro::getCategoria, livro::setCategoria, dto.categoria());
        UpdateUtil.updateIfNotNullAndIfChanged(livro::getDataPublicacao, livro::setDataPublicacao, dto.dataPublicacao());

        livro = livroRepository.save(livro);
        return livroMapper.toResponse(livro);

    }

    @Transactional
    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro", id));
        livroRepository.delete(livro);
    }
}
