package lohan.seletivo.artist.dto;

import java.util.List;
import lohan.seletivo.album.dto.AlbumImageResponse;

public record ArtistAlbumDetailResponse(
        Long id,
        String titulo,
        String url,
        List<AlbumImageResponse> capas
) {
}
