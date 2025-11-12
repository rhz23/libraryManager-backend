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
public class GoogleBooksItemDTO {

    private String id;
    private String kind;
    private VolumeInfo volumeInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {

        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private List<IndustryIdentifier> industryIdentifiers;
        private Integer pageCount;
        private List<String> categories;
        private ImageLinks imageLinks;
        private String language;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IndustryIdentifier {
            private String type; // ISBN_10, ISBN_13
            private String identifier;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ImageLinks {
            private String smallThumbnail;
            private String thumbnail;
        }
    }
}

