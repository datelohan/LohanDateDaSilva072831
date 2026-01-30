package lohan.seletivo.regional.dto;

import java.time.OffsetDateTime;

public record RegionalResponse(
        Long seqId,
        Integer id,
        String nome,
        Boolean ativo,
        OffsetDateTime criadoEm
) {
}
