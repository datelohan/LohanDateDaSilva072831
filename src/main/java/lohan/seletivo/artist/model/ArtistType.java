package lohan.seletivo.artist.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum ArtistType {
    SINGER("CANTOR"),
    BAND("BANDA");

    private final String jsonValue;

    ArtistType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static ArtistType fromValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CANTOR", "SINGER" -> SINGER;
            case "BANDA", "BAND" -> BAND;
            default -> throw new IllegalArgumentException("Tipo de artista invalido: " + value);
        };
    }
}
