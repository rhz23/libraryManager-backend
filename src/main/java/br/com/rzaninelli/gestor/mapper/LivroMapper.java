package br.com.rzaninelli.gestor.mapper;

import br.com.rzaninelli.gestor.model.dto.request.LivroRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.LivroResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LivroMapper {

    Livro toEntity(LivroRequestDTO dto);

    LivroResponseDTO toResponse(Livro entity);

    void updateFromDto(LivroRequestDTO dto, @MappingTarget Livro entity);
}
