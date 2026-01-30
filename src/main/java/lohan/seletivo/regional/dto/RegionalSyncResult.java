package lohan.seletivo.regional.dto;

public record RegionalSyncResult(
        int inseridos,
        int inativados,
        int atualizados
) {
}
