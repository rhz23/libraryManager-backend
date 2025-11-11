package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.exception.custom.DatabaseException;
import br.com.rzaninelli.gestor.exception.custom.DuplicateResourceException;
import br.com.rzaninelli.gestor.exception.custom.ResourceNotFoundException;
import br.com.rzaninelli.gestor.mapper.UsuarioMapper;
import br.com.rzaninelli.gestor.model.dto.request.UsuarioRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.UsuarioResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
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
public class UsuarioService {

    //TODO - criar configuração de cache se sobrar tempo

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        log.info("Criando usuário com email: {}", dto.email());

        usuarioRepository.findByEmail(dto.email()).ifPresent(usuario -> {
            throw new DuplicateResourceException("Usuario", "email", dto.email());
        });

        try {
            Usuario usuario = usuarioMapper.toEntity(dto);
            usuario = usuarioRepository.save(usuario);
            log.info("Usuário criado com sucesso. ID: {}", usuario.getId());
            return usuarioMapper.toResponse(usuario);
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            log.error("Erro ao criar usuário", ex);
            throw new DatabaseException("Erro ao persistir usuário no banco de dados", ex);
        }
    }

    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário", id)
        );
        return usuarioMapper.toResponse(usuario);
    }

    public Page<UsuarioResponseDTO> buscarTodos(Pageable pageable) {

        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponse);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        UpdateUtil.updateIfNotNullAndIfChanged(usuario::getNome, usuario::setNome, dto.nome());
        UpdateUtil.updateIfNotNullAndIfChanged(usuario::getEmail, usuario::setEmail, dto.email());
        UpdateUtil.updateIfNotNullAndIfChanged(usuario::getTelefone, usuario::setTelefone, dto.telefone());

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

}
