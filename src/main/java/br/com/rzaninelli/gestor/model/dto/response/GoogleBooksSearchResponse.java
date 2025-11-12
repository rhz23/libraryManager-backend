package br.com.rzaninelli.gestor.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksSearchResponse {

    private String kind;
    private Integer totalItems;
    private List<GoogleBooksItemDTO> items;
}
