package lohan.seletivo.album.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AlbumResponse(
        Long id,
        String titulo,
        String url,
        OffsetDateTime criadoEm,
        List<AlbumArtistResponse> artistas
) {
}
