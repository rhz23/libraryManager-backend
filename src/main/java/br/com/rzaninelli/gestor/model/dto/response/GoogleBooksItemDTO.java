package br.com.rzaninelli.gestor.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksItemDTO(String id, String kind, VolumeInfo volumeInfo) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(
            String title,
            List<String> authors,
            String publisher,
            String publishedDate,
            String description,
            List<IndustryIdentifier> industryIdentifiers,
            Integer pageCount,
            List<String> categories,
            ImageLinks imageLinks,
            String language
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record IndustryIdentifier(String type, String identifier) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ImageLinks(String smallThumbnail, String thumbnail) {}
    }
}

