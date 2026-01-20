package lohan.seletivo.artist.repository;


import lohan.seletivo.artist.model.Artist;
import lohan.seletivo.artist.model.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Artist> findByType(ArtistType type, Pageable pageable);

    Page<Artist> findByNameContainingIgnoreCaseAndType(String name, ArtistType type, Pageable pageable);
}
