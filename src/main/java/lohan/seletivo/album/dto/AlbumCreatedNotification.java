package lohan.seletivo.album.dto;

import java.time.OffsetDateTime;

public record AlbumCreatedNotification(
        Long id,
        String titulo,
        OffsetDateTime criadoEm
) {
}
