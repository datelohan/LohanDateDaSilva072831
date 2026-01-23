package lohan.seletivo.album.repository;

import lohan.seletivo.album.model.Album;
import lohan.seletivo.artist.model.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    @EntityGraph(attributePaths = "artists")
    @Query("SELECT DISTINCT a FROM Album a JOIN a.artists ar "
            + "WHERE (:type IS NULL OR ar.type = :type) "
            + "AND (:title IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')))"
    )
    Page<Album> search(@Param("type") ArtistType type, @Param("title") String title, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "artists")
    Page<Album> findAll(Pageable pageable);
}
