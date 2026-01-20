package lohan.seletivo.artist.service;


import lohan.seletivo.artist.dto.ArtistCreateRequest;
import lohan.seletivo.artist.dto.ArtistResponse;
import lohan.seletivo.artist.dto.ArtistUpdateRequest;
import lohan.seletivo.artist.model.Artist;
import lohan.seletivo.artist.model.ArtistType;
import lohan.seletivo.artist.repository.ArtistRepository;
import lohan.seletivo.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
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
        boolean hasQ = q != null && !q.trim().isEmpty();

        Page<Artist> page;
        if (hasQ && type != null) {
            page = artistRepository.findByNameContainingIgnoreCaseAndType(q.trim(), type, pageable);
        } else if (hasQ) {
            page = artistRepository.findByNameContainingIgnoreCase(q.trim(), pageable);
        } else if (type != null) {
            page = artistRepository.findByType(type, pageable);
        } else {
            page = artistRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    private Artist getOrThrow(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found: " + id));
    }

    private ArtistResponse toResponse(Artist a) {
        return new ArtistResponse(
                a.getId(),
                a.getName(),
                a.getType(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
