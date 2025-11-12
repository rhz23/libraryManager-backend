package br.com.rzaninelli.gestor.mapper;

import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksItemDTO;
import br.com.rzaninelli.gestor.model.dto.response.GoogleBooksResponseDTO;
import br.com.rzaninelli.gestor.model.entity.Livro;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Component
public interface GoogleBooksMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "volumeInfo.title")
    @Mapping(target = "autor", expression = "java(extractAuthors(item.getVolumeInfo()))")
    @Mapping(target = "isbn", expression = "java(extractIsbn(item.getVolumeInfo()))")
    @Mapping(target = "dataPublicacao", expression = "java(parseDate(item.getVolumeInfo().getPublishedDate()))")
    @Mapping(target = "categoria", expression = "java(extractCategory(item.getVolumeInfo()))")
    @Mapping(target = "descricao", source = "volumeInfo.description")
    @Mapping(target = "imagemUrl", expression = "java(extractImageUrl(item.getVolumeInfo()))")
    @Mapping(target = "numeroPaginas", source = "volumeInfo.pageCount")
    @Mapping(target = "idioma", source = "volumeInfo.language")
    GoogleBooksResponseDTO toDTO(GoogleBooksItemDTO item);

    List<GoogleBooksResponseDTO> toDTOList(List<GoogleBooksItemDTO> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "titulo", source = "volumeInfo.title")
    @Mapping(target = "autor", expression = "java(extractAuthors(item.getVolumeInfo()))")
    @Mapping(target = "isbn", expression = "java(extractIsbn(item.getVolumeInfo()))")
    @Mapping(target = "dataPublicacao", expression = "java(parseDate(item.getVolumeInfo().getPublishedDate()))")
    @Mapping(target = "categoria", expression = "java(extractCategory(item.getVolumeInfo()))")
    @Mapping(target = "googleBooksId", source = "id")
    @Mapping(target = "disponivel", constant = "true")
    @Mapping(target = "emprestimos", ignore = true)
    Livro toEntity(GoogleBooksItemDTO item);

    default String extractAuthors(GoogleBooksItemDTO.VolumeInfo volumeInfo) {
        if (volumeInfo.getAuthors() == null || volumeInfo.getAuthors().isEmpty()) {
            return "Autor Desconhecido";
        }
        return String.join(", ", volumeInfo.getAuthors());
    }

    default String extractIsbn(GoogleBooksItemDTO.VolumeInfo volumeInfo) {
        if (volumeInfo.getIndustryIdentifiers() == null) {
            return generateRandomIsbn();
        }

        return volumeInfo.getIndustryIdentifiers().stream()
                .filter(id -> "ISBN_13".equals(id.getType()))
                .map(GoogleBooksItemDTO.VolumeInfo.IndustryIdentifier::getIdentifier)
                .findFirst()
                .orElseGet(() -> volumeInfo.getIndustryIdentifiers().stream()
                        .filter(id -> "ISBN_10".equals(id.getType()))
                        .map(GoogleBooksItemDTO.VolumeInfo.IndustryIdentifier::getIdentifier)
                        .findFirst()
                        .orElse(generateRandomIsbn()));
    }

    default String extractCategory(GoogleBooksItemDTO.VolumeInfo volumeInfo) {
        if (volumeInfo.getCategories() == null || volumeInfo.getCategories().isEmpty()) {
            return "Geral";
        }
        return volumeInfo.getCategories().get(0);
    }

    default String extractImageUrl(GoogleBooksItemDTO.VolumeInfo volumeInfo) {
        if (volumeInfo.getImageLinks() == null) {
            return null;
        }
        return volumeInfo.getImageLinks().getThumbnail();
    }

    default LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDate.now();
        }

        try {
            if (dateStr.length() == 4) {
                return LocalDate.of(Integer.parseInt(dateStr), 1, 1);
            }
            if (dateStr.length() == 7) {
                return LocalDate.parse(dateStr + "-01");
            }
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    default String generateRandomIsbn() {
        return UUID.randomUUID().toString().substring(0, 13).replaceAll("-", "");
    }
}
