package lohan.seletivo.album.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AlbumDetailResponse(
        Long id,
        String titulo,
        String url,
        OffsetDateTime criadoEm,
        List<AlbumArtistResponse> artistas,
        List<AlbumImageResponse> capas
) {
}
