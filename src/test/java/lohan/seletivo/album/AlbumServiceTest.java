
package lohan.seletivo.album;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import lohan.seletivo.album.dto.AlbumCreateRequest;
import lohan.seletivo.album.model.Album;
import lohan.seletivo.album.service.AlbumNotificationService;
import lohan.seletivo.album.service.AlbumService;
import lohan.seletivo.album.repository.AlbumRepository;
import lohan.seletivo.artist.model.Artist;
import lohan.seletivo.artist.repository.ArtistRepository;
import lohan.seletivo.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumNotificationService albumNotificationService;

    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        albumService = new AlbumService(albumRepository, artistRepository, albumNotificationService);
    }

    @Test
    void createAlbumNotifies() {
        Artist artist = new Artist();
        artist.setId(1L);
        artist.setName("Serj Tankian");

        when(artistRepository.findAllById(eq(Set.of(1L)))).thenReturn(List.of(artist));
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlbumCreateRequest request = new AlbumCreateRequest("Harakiri", Set.of(1L));
        Album saved = albumService.create(request);

        assertEquals("Harakiri", saved.getTitle());
        verify(albumNotificationService).notifyAlbumCreated(saved);
    }

    @Test
    void createAlbumThrowsWhenArtistMissing() {
        when(artistRepository.findAllById(eq(Set.of(99L)))).thenReturn(List.of());

        AlbumCreateRequest request = new AlbumCreateRequest("Teste", Set.of(99L));
        assertThrows(NotFoundException.class, () -> albumService.create(request));

        verify(albumRepository, never()).save(any(Album.class));
    }
}
