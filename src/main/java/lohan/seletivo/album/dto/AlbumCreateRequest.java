package lohan.seletivo.album.dto;

import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlbumCreateRequest(
        @NotBlank
        @Size(max = 200)
        @Schema(example = "Post Traumatic")
        String titulo,
        @Schema(example = "[1,2]")
        Set<Long> artistIds
) {
}
