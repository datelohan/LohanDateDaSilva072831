package lohan.seletivo.artist.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ArtistTypeConverter implements AttributeConverter<ArtistType, String> {

    @Override
    public String convertToDatabaseColumn(ArtistType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public ArtistType convertToEntityAttribute(String dbData) {
        return ArtistType.fromValue(dbData);
    }
}
