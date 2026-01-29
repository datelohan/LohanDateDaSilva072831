package lohan.seletivo.artist.service;


import java.util.List;
import java.util.stream.Collectors;
import lohan.seletivo.album.model.Album;
import lohan.seletivo.album.service.AlbumImageService;
import lohan.seletivo.album.repository.AlbumRepository;
import lohan.seletivo.album.dto.AlbumImageResponse;
import lohan.seletivo.artist.dto.ArtistAlbumDetailResponse;
import lohan.seletivo.artist.dto.ArtistCreateRequest;
import lohan.seletivo.artist.dto.ArtistDetailResponse;
import lohan.seletivo.artist.dto.ArtistResponse;
import lohan.seletivo.artist.dto.ArtistUpdateRequest;
import lohan.seletivo.artist.model.Artist;
import lohan.seletivo.artist.model.ArtistType;
import lohan.seletivo.artist.repository.ArtistRepository;
import lohan.seletivo.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final AlbumImageService albumImageService;

    public ArtistService(ArtistRepository artistRepository,
            AlbumRepository albumRepository,
            AlbumImageService albumImageService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.albumImageService = albumImageService;
    }

    public ArtistResponse create(ArtistCreateRequest req) {
        Artist artist = new Artist();
        artist.setName(req.name());
        artist.setType(req.type());

        Artist saved = artistRepository.save(artist);
        return toResponse(saved);
    }

    public ArtistResponse update(Long id, ArtistUpdateRequest req) {
        Artist artist = getOrThrow(id);
        artist.setName(req.name());
        artist.setType(req.type());

        Artist saved = artistRepository.save(artist);
        return toResponse(saved);
    }

    public ArtistResponse getById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public Page<ArtistResponse> list(String q, ArtistType type, Pageable pageable) {
        return listEntities(q, type, pageable).map(this::toResponse);
    }

    public ArtistDetailResponse getDetails(Long id) {
        return toDetailResponse(getOrThrow(id));
    }

    public Page<ArtistDetailResponse> listDetails(String q, ArtistType type, Pageable pageable) {
        return listEntities(q, type, pageable).map(this::toDetailResponse);
    }

    private Artist getOrThrow(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found: " + id));
    }

    private Page<Artist> listEntities(String q, ArtistType type, Pageable pageable) {
        boolean hasQ = q != null && !q.trim().isEmpty();

        if (hasQ && type != null) {
            return artistRepository.findByNameContainingIgnoreCaseAndType(q.trim(), type, pageable);
        }
        if (hasQ) {
            return artistRepository.findByNameContainingIgnoreCase(q.trim(), pageable);
        }
        if (type != null) {
            return artistRepository.findByType(type, pageable);
        }
        return artistRepository.findAll(pageable);
    }

    private ArtistResponse toResponse(Artist a) {
        return new ArtistResponse(
                a.getId(),
                a.getName(),
                buildArtistUrl(a.getId()),
                a.getType(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }

    private ArtistDetailResponse toDetailResponse(Artist a) {
        List<Album> albums = albumRepository.findByArtistId(a.getId());
        List<ArtistAlbumDetailResponse> albuns = albums.stream()
                .map(album -> new ArtistAlbumDetailResponse(
                        album.getId(),
                        album.getTitle(),
                        buildAlbumUrl(album.getId()),
                        albumImageService.list(album.getId())
                ))
                .collect(Collectors.toList());

        return new ArtistDetailResponse(
                a.getId(),
                a.getName(),
                buildArtistUrl(a.getId()),
                a.getType(),
                a.getCreatedAt(),
                a.getUpdatedAt(),
                albuns
        );
    }

    private String buildArtistUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/artists/{id}")
                .buildAndExpand(id)
                .toUriString();
    }

    private String buildAlbumUrl(Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/albums/{id}")
                .buildAndExpand(id)
                .toUriString();
    }
}
