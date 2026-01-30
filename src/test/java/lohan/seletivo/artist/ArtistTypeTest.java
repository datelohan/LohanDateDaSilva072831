package lohan.seletivo.artist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lohan.seletivo.artist.model.ArtistType;
import org.junit.jupiter.api.Test;

class ArtistTypeTest {

    @Test
    void fromValueMapsPtBrAndEn() {
        assertEquals(ArtistType.SINGER, ArtistType.fromValue("CANTOR"));
        assertEquals(ArtistType.SINGER, ArtistType.fromValue("singer"));
        assertEquals(ArtistType.BAND, ArtistType.fromValue("BANDA"));
        assertEquals(ArtistType.BAND, ArtistType.fromValue("band"));
    }

    @Test
    void fromValueThrowsOnInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ArtistType.fromValue("X"));
    }
}
