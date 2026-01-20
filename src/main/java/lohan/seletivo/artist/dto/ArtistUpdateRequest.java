package lohan.seletivo.artist.dto;

import lohan.seletivo.artist.model.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArtistUpdateRequest(
        @NotBlank @Size(min = 2, max = 120) String name,
        @NotNull ArtistType type
) {}
