package lohan.seletivo.album.dto;

import lohan.seletivo.artist.model.ArtistType;

public record AlbumArtistResponse(Long id, String nome, ArtistType tipo, String url) {
}
