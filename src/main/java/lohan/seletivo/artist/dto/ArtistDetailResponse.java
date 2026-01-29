package lohan.seletivo.artist.dto;

import java.time.OffsetDateTime;
import java.util.List;
import lohan.seletivo.artist.model.ArtistType;

public record ArtistDetailResponse(
        Long id,
        String name,
        String url,
        ArtistType type,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<ArtistAlbumDetailResponse> albuns
) {
}
