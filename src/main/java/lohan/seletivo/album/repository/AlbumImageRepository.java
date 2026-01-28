package lohan.seletivo.album.repository;

import java.util.List;
import lohan.seletivo.album.model.AlbumImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {

    List<AlbumImage> findByAlbumId(Long albumId);
}
