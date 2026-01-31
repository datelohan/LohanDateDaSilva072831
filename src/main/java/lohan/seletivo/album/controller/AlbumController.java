package lohan.seletivo.album.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import lohan.seletivo.album.dto.AlbumArtistResponse;
import lohan.seletivo.album.dto.AlbumCreateRequest;
import lohan.seletivo.album.dto.AlbumDetailResponse;
import lohan.seletivo.album.dto.AlbumImageResponse;
import lohan.seletivo.album.dto.AlbumResponse;
import lohan.seletivo.album.dto.AlbumUpdateRequest;
import lohan.seletivo.album.model.Album;
import lohan.seletivo.album.service.AlbumImageService;
import lohan.seletivo.album.service.AlbumService;
import lohan.seletivo.artist.model.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumImageService albumImageService;

    public AlbumController(AlbumService albumService, AlbumImageService albumImageService) {
        this.albumService = albumService;
        this.albumImageService = albumImageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um album")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Album criado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":10,\"titulo\":\"Harakiri\",\"url\":\"http://localhost:8080/api/v1/albums/10\",\"criadoEm\":\"2026-01-30T18:00:00Z\",\"artistas\":[{\"id\":1,\"nome\":\"Serj Tankian\",\"tipo\":\"CANTOR\",\"url\":\"http://localhost:8080/api/v1/artists/1\"}]}"
                    )))
    })
    public AlbumResponse create(@Valid @RequestBody AlbumCreateRequest request) {
        return toResponse(albumService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album atualizado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":10,\"titulo\":\"Harakiri (Deluxe)\",\"url\":\"http://localhost:8080/api/v1/albums/10\",\"criadoEm\":\"2026-01-30T18:00:00Z\",\"artistas\":[{\"id\":1,\"nome\":\"Serj Tankian\",\"tipo\":\"CANTOR\",\"url\":\"http://localhost:8080/api/v1/artists/1\"}]}"
                    )))
    })
    public AlbumResponse update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateRequest request) {
        return toResponse(albumService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca album por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album encontrado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":10,\"titulo\":\"Harakiri\",\"url\":\"http://localhost:8080/api/v1/albums/10\",\"criadoEm\":\"2026-01-30T18:00:00Z\",\"artistas\":[{\"id\":1,\"nome\":\"Serj Tankian\",\"tipo\":\"CANTOR\",\"url\":\"http://localhost:8080/api/v1/artists/1\"}]}"
                    )))
    })
    public AlbumResponse getById(@PathVariable Long id) {
        return toResponse(albumService.getById(id));
    }

    @GetMapping("/details")
    @Operation(summary = "Lista albuns com relacionamentos e capas")
    public Page<AlbumDetailResponse> listDetails(
            @Parameter(description = "Filtro por titulo", example = "Harakiri")
            @RequestParam(name = "titulo", required = false) String titulo,
            @Parameter(description = "Tipo do artista (filtra albuns)", example = "CANTOR")
            @RequestParam(name = "tipo", required = false) String tipo,
            @Parameter(description = "Pagina (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da pagina", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "Ordenacao no formato campo,direcao", example = "title,asc")
            @RequestParam(name = "sort", required = false) String sort
    ) {
        ArtistType artistType = tipo != null ? ArtistType.fromValue(tipo) : null;
        Pageable pageable = buildPageable(page, size, sort, "title");
        return albumService.listDetails(titulo, artistType, pageable).map(this::toDetailResponse);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "Busca album detalhado por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album detalhado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":10,\"titulo\":\"Harakiri\",\"url\":\"http://localhost:8080/api/v1/albums/10\",\"criadoEm\":\"2026-01-30T18:00:00Z\",\"artistas\":[{\"id\":1,\"nome\":\"Serj Tankian\",\"tipo\":\"CANTOR\",\"url\":\"http://localhost:8080/api/v1/artists/1\"}],\"capas\":[{\"id\":5,\"nomeArquivo\":\"capa.jpg\",\"contentType\":\"image/jpeg\",\"tamanhoBytes\":12345,\"url\":\"https://minio/...\",\"expiraEm\":\"2026-01-30T18:30:00Z\"}]}"
                    )))
    })
    public AlbumDetailResponse getDetails(@PathVariable Long id) {
        return toDetailResponse(albumService.getById(id));
    }

    @PostMapping(path = "/{id}/covers", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload de capas do album (multipart/form-data)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Capas enviadas",
                    content = @Content(examples = @ExampleObject(
                            value = "[{\"id\":5,\"nomeArquivo\":\"capa.jpg\",\"contentType\":\"image/jpeg\",\"tamanhoBytes\":12345,\"url\":\"https://minio/...\",\"expiraEm\":\"2026-01-30T18:30:00Z\"}]"
                    )))
    })
    public List<AlbumImageResponse> uploadCovers(@PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        return albumImageService.upload(id, files);
    }

    @GetMapping("/{id}/covers")
    @Operation(summary = "Lista capas do album")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Capas do album",
                    content = @Content(examples = @ExampleObject(
                            value = "[{\"id\":5,\"nomeArquivo\":\"capa.jpg\",\"contentType\":\"image/jpeg\",\"tamanhoBytes\":12345,\"url\":\"https://minio/...\",\"expiraEm\":\"2026-01-30T18:30:00Z\"}]"
                    )))
    })
    public List<AlbumImageResponse> listCovers(@PathVariable Long id) {
        return albumImageService.list(id);
    }

    @GetMapping
    @Operation(summary = "Lista albuns com filtros")
    public Page<AlbumResponse> list(
            @Parameter(description = "Filtro por titulo", example = "Harakiri")
            @RequestParam(name = "titulo", required = false) String titulo,
            @Parameter(description = "Tipo do artista (filtra albuns)", example = "CANTOR")
            @RequestParam(name = "tipo", required = false) String tipo,
            @Parameter(description = "Pagina (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da pagina", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "Ordenacao no formato campo,direcao", example = "title,asc")
            @RequestParam(name = "sort", required = false) String sort
    ) {
        ArtistType artistType = tipo != null ? ArtistType.fromValue(tipo) : null;
        Pageable pageable = buildPageable(page, size, sort, "title");
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

    private AlbumDetailResponse toDetailResponse(Album album) {
        List<AlbumArtistResponse> artistas = album.getArtists().stream()
                .map(artist -> new AlbumArtistResponse(
                        artist.getId(),
                        artist.getName(),
                        artist.getType(),
                        buildArtistUrl(artist.getId())
                ))
                .collect(Collectors.toList());

        List<AlbumImageResponse> capas = albumImageService.list(album.getId());

        return new AlbumDetailResponse(
                album.getId(),
                album.getTitle(),
                buildAlbumUrl(album.getId()),
                album.getCreatedAt(),
                artistas,
                capas
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

    private Pageable buildPageable(int page, int size, String sort, String defaultField) {
        Sort sortObj = Sort.by(defaultField).ascending();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            Sort.Direction direction = parts.length > 1
                    ? Sort.Direction.fromString(parts[1].trim())
                    : Sort.Direction.ASC;
            sortObj = Sort.by(direction, field);
        }
        return PageRequest.of(page, size, sortObj);
    }
}
