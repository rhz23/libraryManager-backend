package br.com.rzaninelli.gestor.model.dto.request;

import br.com.rzaninelli.gestor.enums.TipoRecomendacao;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record FiltroRecomendacaoRequestDTO(
        @NotNull(message = "O ID do usuário é obrigatório") Long usuarioId,
        TipoRecomendacao tipo,
        LocalDate inicio,
        LocalDate fim,
        String sortField,
        Sort.Direction direcao
) {
    public FiltroRecomendacaoRequestDTO {
        if (tipo == null) tipo = TipoRecomendacao.CATEGORIAS;
        if (sortField == null) sortField = "titulo";
        if (direcao == null) direcao = Sort.Direction.ASC;
    }
}
