package br.com.rzaninelli.gestor.mapper;

import br.com.rzaninelli.gestor.model.dto.request.UsuarioRequestDTO;
import br.com.rzaninelli.gestor.model.dto.response.UsuarioResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO dto);

    UsuarioResponseDTO toResponse(Usuario entity);

    void updateFromDto(UsuarioRequestDTO dto, @MappingTarget Usuario entity);
}
