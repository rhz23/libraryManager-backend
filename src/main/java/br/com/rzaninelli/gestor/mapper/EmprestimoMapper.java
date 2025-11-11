package br.com.rzaninelli.gestor.mapper;

import br.com.rzaninelli.gestor.model.dto.request.EmprestimoRequestDTO;
import br.com.rzaninelli.gestor.model.dto.request.LivroRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.EmprestimoResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Emprestimo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

    Emprestimo toEntity(LivroRequestDTO dto);

    EmprestimoResponseDTO toResponse(Emprestimo entity);

    void updateFromDto(EmprestimoRequestDTO dto, @MappingTarget Emprestimo entity);
}
