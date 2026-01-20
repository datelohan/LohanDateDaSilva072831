package lohan.seletivo.artist.dto;


import lohan.seletivo.artist.model.ArtistType;
import java.time.OffsetDateTime;

public record ArtistResponse(
        Long id,
        String name,
        ArtistType type,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
