package lohan.seletivo.artist.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lohan.seletivo.artist.dto.ArtistCreateRequest;
import lohan.seletivo.artist.dto.ArtistDetailResponse;
import lohan.seletivo.artist.dto.ArtistResponse;
import lohan.seletivo.artist.dto.ArtistUpdateRequest;
import lohan.seletivo.artist.model.ArtistType;
import lohan.seletivo.artist.service.ArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um artista")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artista criado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":1,\"name\":\"Serj Tankian\",\"url\":\"http://localhost:8080/api/v1/artists/1\",\"type\":\"CANTOR\",\"createdAt\":\"2026-01-30T18:00:00Z\",\"updatedAt\":\"2026-01-30T18:00:00Z\"}"
                    )))
    })
    public ArtistResponse create(@Valid @RequestBody ArtistCreateRequest request) {
        return artistService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um artista")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista atualizado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":1,\"name\":\"Guns N' Roses\",\"url\":\"http://localhost:8080/api/v1/artists/1\",\"type\":\"BANDA\",\"createdAt\":\"2026-01-30T18:00:00Z\",\"updatedAt\":\"2026-01-30T19:00:00Z\"}"
                    )))
    })
    public ArtistResponse update(@PathVariable Long id, @Valid @RequestBody ArtistUpdateRequest request) {
        return artistService.update(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca artista por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista encontrado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":1,\"name\":\"Serj Tankian\",\"url\":\"http://localhost:8080/api/v1/artists/1\",\"type\":\"CANTOR\",\"createdAt\":\"2026-01-30T18:00:00Z\",\"updatedAt\":\"2026-01-30T18:00:00Z\"}"
                    )))
    })
    public ArtistResponse getById(@PathVariable Long id) {
        return artistService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Lista artistas com filtros")
    public Page<ArtistResponse> list(
            @Parameter(description = "Filtro por nome", example = "Mike")
            @RequestParam(required = false) String q,
            @Parameter(description = "Tipo do artista", example = "CANTOR")
            @RequestParam(required = false) ArtistType type,
            @Parameter(description = "Pagina (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da pagina", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "Ordenacao no formato campo,direcao", example = "name,asc")
            @RequestParam(name = "sort", required = false) String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort, "name");
        return artistService.list(q, type, pageable);
    }

    @GetMapping("/details")
    @Operation(summary = "Lista artistas com relacionamentos")
    public Page<ArtistDetailResponse> listDetails(
            @Parameter(description = "Filtro por nome", example = "Serj")
            @RequestParam(required = false) String q,
            @Parameter(description = "Tipo do artista", example = "CANTOR")
            @RequestParam(required = false) ArtistType type,
            @Parameter(description = "Pagina (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Tamanho da pagina", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "Ordenacao no formato campo,direcao", example = "name,asc")
            @RequestParam(name = "sort", required = false) String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort, "name");
        return artistService.listDetails(q, type, pageable);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "Busca artista detalhado por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista detalhado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":1,\"name\":\"Serj Tankian\",\"url\":\"http://localhost:8080/api/v1/artists/1\",\"type\":\"CANTOR\",\"createdAt\":\"2026-01-30T18:00:00Z\",\"updatedAt\":\"2026-01-30T18:00:00Z\",\"albuns\":[{\"id\":10,\"titulo\":\"Harakiri\",\"url\":\"http://localhost:8080/api/v1/albums/10\",\"capas\":[{\"id\":5,\"nomeArquivo\":\"capa.jpg\",\"contentType\":\"image/jpeg\",\"tamanhoBytes\":12345,\"url\":\"https://minio/...\",\"expiraEm\":\"2026-01-30T18:30:00Z\"}]}]}"
                    )))
    })
    public ArtistDetailResponse getDetails(@PathVariable Long id) {
        return artistService.getDetails(id);
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
