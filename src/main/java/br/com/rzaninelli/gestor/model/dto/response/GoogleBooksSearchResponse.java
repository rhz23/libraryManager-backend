package br.com.rzaninelli.gestor.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksSearchResponse(String kind, Integer totalItems, List<GoogleBooksItemDTO> items) {}
