package lohan.seletivo.album.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lohan.seletivo.album.dto.AlbumArtistResponse;
import lohan.seletivo.album.dto.AlbumCreateRequest;
import lohan.seletivo.album.dto.AlbumResponse;
import lohan.seletivo.album.dto.AlbumUpdateRequest;
import lohan.seletivo.album.model.Album;
import lohan.seletivo.album.service.AlbumService;
import lohan.seletivo.artist.model.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse create(@Valid @RequestBody AlbumCreateRequest request) {
        return toResponse(albumService.create(request));
    }

    @PutMapping("/{id}")
    public AlbumResponse update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateRequest request) {
        return toResponse(albumService.update(id, request));
    }

    @GetMapping("/{id}")
    public AlbumResponse getById(@PathVariable Long id) {
        return toResponse(albumService.getById(id));
    }

    @GetMapping
    public Page<AlbumResponse> list(
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "tipo", required = false) String tipo,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults(@SortDefault(sort = "title")) Pageable pageable
    ) {
        ArtistType artistType = tipo != null ? ArtistType.fromValue(tipo) : null;
        return albumService.list(titulo, artistType, pageable).map(this::toResponse);
    }

    private AlbumResponse toResponse(Album album) {
        List<AlbumArtistResponse> artistas = album.getArtists().stream()
                .map(artist -> new AlbumArtistResponse(
                        artist.getId(),
                        artist.getName(),
                        artist.getType(),
                        buildArtistUrl(artist.getId())
                ))
                .collect(Collectors.toList());

        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                buildAlbumUrl(album.getId()),
                album.getCreatedAt(),
                artistas
        );
    }

    private String buildAlbumUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/albums/{id}")
                .buildAndExpand(id)
                .toUriString();
    }

    private String buildArtistUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/artists/{id}")
                .buildAndExpand(id)
                .toUriString();
    }
}
