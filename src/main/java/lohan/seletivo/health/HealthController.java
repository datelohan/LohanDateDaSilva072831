package lohan.seletivo.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/liveness")
    @Operation(summary = "Liveness probe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicacao viva",
                    content = @Content(examples = @ExampleObject(value = "{\"status\":\"UP\"}")))
    })
    public HealthDescriptor liveness() {
        return healthEndpoint.healthForPath("liveness");
    }

    @GetMapping("/readiness")
    @Operation(summary = "Readiness probe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicacao pronta",
                    content = @Content(examples = @ExampleObject(value = "{\"status\":\"UP\"}")))
    })
    public HealthDescriptor readiness() {
        return healthEndpoint.healthForPath("readiness");
    }
}
