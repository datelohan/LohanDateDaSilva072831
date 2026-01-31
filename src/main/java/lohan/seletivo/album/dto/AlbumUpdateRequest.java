package lohan.seletivo.album.dto;

import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlbumUpdateRequest(
        @NotBlank
        @Size(max = 200)
        @Schema(example = "Use Your Illusion I")
        String titulo,
        @Schema(example = "[3]")
        Set<Long> artistIds
) {
}
