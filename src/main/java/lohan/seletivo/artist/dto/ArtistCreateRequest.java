package lohan.seletivo.artist.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lohan.seletivo.artist.model.ArtistType;

public record ArtistCreateRequest(
        @NotBlank
        @Size(min = 2, max = 120)
        @Schema(example = "Mike Shinoda")
        String name,
        @NotNull
        @Schema(example = "CANTOR")
        ArtistType type
) {}
