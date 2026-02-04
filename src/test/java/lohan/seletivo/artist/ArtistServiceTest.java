package lohan.seletivo.artist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import lohan.seletivo.album.repository.AlbumRepository;
import lohan.seletivo.album.service.AlbumImageService;
import lohan.seletivo.artist.model.ArtistType;
import lohan.seletivo.artist.repository.ArtistRepository;
import lohan.seletivo.artist.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumImageService albumImageService;

    private ArtistService service;

    @BeforeEach
    void setUp() {
        service = new ArtistService(artistRepository, albumRepository, albumImageService);
    }

    @Test
    void list_usesNameAndType_whenBothProvided() {
        when(artistRepository.findByNameContainingIgnoreCaseAndType(any(), any(), any(PageRequest.class)))
                .thenReturn(Page.empty());

        service.list("serj", ArtistType.SINGER, PageRequest.of(0, 10));

        verify(artistRepository).findByNameContainingIgnoreCaseAndType(eq("serj"), eq(ArtistType.SINGER), any(PageRequest.class));
        verifyNoMoreInteractions(artistRepository);
    }

    @Test
    void list_usesNameOnly_whenOnlyNameProvided() {
        when(artistRepository.findByNameContainingIgnoreCase(any(), any(PageRequest.class))).thenReturn(Page.empty());

        service.list("serj", null, PageRequest.of(0, 10));

        verify(artistRepository).findByNameContainingIgnoreCase(eq("serj"), any(PageRequest.class));
        verifyNoMoreInteractions(artistRepository);
    }

    @Test
    void list_usesTypeOnly_whenOnlyTypeProvided() {
        when(artistRepository.findByType(any(), any(PageRequest.class))).thenReturn(Page.empty());

        service.list(null, ArtistType.BAND, PageRequest.of(0, 10));

        verify(artistRepository).findByType(eq(ArtistType.BAND), any(PageRequest.class));
        verifyNoMoreInteractions(artistRepository);
    }

    @Test
    void list_usesFindAll_whenNoFilters() {
        when(artistRepository.findAll(any(PageRequest.class))).thenReturn(Page.empty());

        service.list(null, null, PageRequest.of(0, 10));

        verify(artistRepository).findAll(any(PageRequest.class));
        verifyNoMoreInteractions(artistRepository);
    }
}
