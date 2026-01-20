package lohan.seletivo.artist.controller;

import lohan.seletivo.artist.dto.ArtistCreateRequest;
import lohan.seletivo.artist.dto.ArtistResponse;
import lohan.seletivo.artist.dto.ArtistUpdateRequest;
import lohan.seletivo.artist.model.ArtistType;
import lohan.seletivo.artist.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ArtistResponse create(@Valid @RequestBody ArtistCreateRequest request) {
        return artistService.create(request);
    }

    @PutMapping("/{id}")
    public ArtistResponse update(@PathVariable Long id, @Valid @RequestBody ArtistUpdateRequest request) {
        return artistService.update(id, request);
    }

    @GetMapping("/{id}")
    public ArtistResponse getById(@PathVariable Long id) {
        return artistService.getById(id);
    }

    @GetMapping
    public Page<ArtistResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ArtistType type,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return artistService.list(q, type, pageable);
    }
}
