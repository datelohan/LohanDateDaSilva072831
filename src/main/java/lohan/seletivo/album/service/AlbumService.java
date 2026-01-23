package lohan.seletivo.album.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lohan.seletivo.album.dto.AlbumCreateRequest;
import lohan.seletivo.album.dto.AlbumUpdateRequest;
import lohan.seletivo.album.model.Album;
import lohan.seletivo.album.repository.AlbumRepository;
import lohan.seletivo.artist.model.Artist;
import lohan.seletivo.artist.model.ArtistType;
import lohan.seletivo.artist.repository.ArtistRepository;
import lohan.seletivo.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public Album create(AlbumCreateRequest request) {
        Album album = new Album();
        album.setTitle(request.titulo());
        album.setArtists(loadArtists(request.artistIds()));
        return albumRepository.save(album);
    }

    public Album update(Long id, AlbumUpdateRequest request) {
        Album album = getById(id);
        album.setTitle(request.titulo());
        album.setArtists(loadArtists(request.artistIds()));
        return albumRepository.save(album);
    }

    public Album getById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album nao encontrado: " + id));
    }

    public Page<Album> list(String titulo, ArtistType tipo, Pageable pageable) {
        String normalizedTitle = (titulo == null || titulo.isBlank()) ? null : titulo.trim();
        if (normalizedTitle == null && tipo == null) {
            return albumRepository.findAll(pageable);
        }
        return albumRepository.search(tipo, normalizedTitle, pageable);
    }

    private Set<Artist> loadArtists(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<Artist> artists = artistRepository.findAllById(ids);
        if (artists.size() != ids.size()) {
            Set<Long> found = artists.stream().map(Artist::getId).collect(Collectors.toSet());
            Set<Long> missing = ids.stream()
                    .filter(id -> !found.contains(id))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            throw new NotFoundException("Artistas nao encontrados: " + missing);
        }
        return new LinkedHashSet<>(artists);
    }
}
