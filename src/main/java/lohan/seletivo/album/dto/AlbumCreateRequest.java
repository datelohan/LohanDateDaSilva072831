package lohan.seletivo.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record AlbumCreateRequest(
        @NotBlank @Size(max = 200) String titulo,
        Set<Long> artistIds
) {
}
