package com.sportygroup.aviation.api.wrapper.controller;

import com.sportygroup.aviation.api.wrapper.dto.response.AirportResponse;
import com.sportygroup.aviation.api.wrapper.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP controller. Only delegates to the service—no business logic.
 */
@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
@Tag(name = "Airports", description = "Airport lookup by ICAO code")
public class AirportController {

    private final AirportService airportService;

    @GetMapping(value = "/{icao}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get airport by ICAO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Airport found",
                    content = @Content(schema = @Schema(implementation = AirportResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ICAO"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public AirportResponse getByIcao(
            @Parameter(description = "ICAO code (4 letters)", example = "SBSP", required = true)
            @PathVariable String icao) {
        return airportService.getByIcao(icao);
    }
}
