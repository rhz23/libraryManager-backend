package br.com.rzaninelli.gestor.service;

import br.com.rzaninelli.gestor.enums.StatusEmprestimo;
import br.com.rzaninelli.gestor.exception.custom.BusinessException;
import br.com.rzaninelli.gestor.exception.custom.ResourceNotFoundException;
import br.com.rzaninelli.gestor.mapper.EmprestimoMapper;
import br.com.rzaninelli.gestor.model.dto.request.EmprestimoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.EmprestimoResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Emprestimo;
import br.com.rzaninelli.gestor.model.entity.Livro;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import br.com.rzaninelli.gestor.repository.EmprestimoRepository;
import br.com.rzaninelli.gestor.repository.LivroRepository;
import br.com.rzaninelli.gestor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final JmsTemplate jmsTemplate;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional
    public EmprestimoResponseDTO realizarEmprestimo(EmprestimoRequestDTO dto) {
        log.info("Realizando empréstimo - Usuário: {}, Livro: {}",
                dto.usuarioId(), dto.livroId());

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", dto.usuarioId()));

        Livro livro = livroRepository.findById(dto.livroId())
                .orElseThrow(() -> new ResourceNotFoundException("Livro",  dto.livroId()));

        if (!livro.getDisponivel()) {
            throw new BusinessException("Livro não está disponível");
        }

        emprestimoRepository.findByLivroIdAndStatus(livro.getId(), StatusEmprestimo.ATIVO)
                .ifPresent(e -> {
                    throw new BusinessException("Livro já está emprestado");
                });

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo = emprestimoRepository.save(emprestimo);

        livro.setDisponivel(false);
        livroRepository.save(livro);

        enviarMensagemEmprestimo(emprestimo);

        return emprestimoMapper.toResponse(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO realizarDevolucao(Long emprestimoId) {
        log.info("Realizando devolução - Empréstimo: {}", emprestimoId);

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo",  emprestimoId));

        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
            throw new BusinessException("Empréstimo não está ativo");
        }

        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo = emprestimoRepository.save(emprestimo);

        Livro livro = emprestimo.getLivro();
        livro.setDisponivel(true);
        livroRepository.save(livro);

        return emprestimoMapper.toResponse(emprestimo);
    }

    public Page<EmprestimoResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return emprestimoRepository.findByUsuarioId(usuarioId, pageable)
                .map(emprestimoMapper::toResponse);
    }

    //TODO - retirar se não sobrar tempo para implementar o envio de push/email
    private void enviarMensagemEmprestimo(Emprestimo emprestimo) {
        try {
            String mensagem = String.format("Empréstimo realizado - Usuário: %s, Livro: %s",
                    emprestimo.getUsuario().getNome(),
                    emprestimo.getLivro().getTitulo());
            jmsTemplate.convertAndSend("biblioteca.emprestimos", mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem: {}", e.getMessage());
        }
    }
}
