package lohan.seletivo.album.dto;

import java.time.OffsetDateTime;

public record AlbumImageResponse(
        Long id,
        String nomeArquivo,
        String contentType,
        Long tamanhoBytes,
        String url,
        OffsetDateTime expiraEm
) {
}
