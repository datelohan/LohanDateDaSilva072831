package lohan.seletivo.regional.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import lohan.seletivo.regional.dto.RegionalResponse;
import lohan.seletivo.regional.dto.RegionalSyncResult;
import lohan.seletivo.regional.model.Regional;
import lohan.seletivo.regional.service.RegionalSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/regionais")
public class RegionalController {

    private final RegionalSyncService regionalSyncService;

    public RegionalController(RegionalSyncService regionalSyncService) {
        this.regionalSyncService = regionalSyncService;
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sincroniza regionais com o endpoint externo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sincronizacao realizada",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"inseridos\":3,\"inativados\":1,\"atualizados\":2}"
                    )))
    })
    public RegionalSyncResult sync() {
        return regionalSyncService.sync();
    }

    @GetMapping
    @Operation(summary = "Lista regionais")
    public List<RegionalResponse> listar(
            @Parameter(description = "Filtra por status ativo", example = "true")
            @RequestParam(name = "ativo", required = false) Boolean ativo) {
        List<Regional> regionais = regionalSyncService.listar(ativo);
        return regionais.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RegionalResponse toResponse(Regional regional) {
        return new RegionalResponse(
                regional.getSeqId(),
                regional.getRegionalId(),
                regional.getNome(),
                regional.getAtivo(),
                regional.getCriadoEm()
        );
    }
}
