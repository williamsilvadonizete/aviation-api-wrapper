package com.sportygroup.aviation.api.wrapper.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportygroup.aviation.api.wrapper.dto.external.AviationApiAirportDto;
import com.sportygroup.aviation.api.wrapper.service.provider.AviationProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Integration tests: Controller + Service with mocked provider (no real HTTP calls).
 * Scenarios: invalid ICAO (400), not found (404), provider failure (503), success (200).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AirportIntegrationTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private AviationProvider aviationProvider;

    private RestClient restClient() {
        return RestClient.create("http://localhost:" + port);
    }

    @Test
    void getByIcao_invalidCode_returns400() {
        int status = restClient().get()
                .uri("/api/v1/airports/XX")
                .exchange((req, res) -> res.getStatusCode().value());
        assertThat(status).isEqualTo(400);
    }

    @Test
    void getByIcao_notFound_returns404() {
        when(aviationProvider.getByIcao(eq("XXXX"))).thenReturn(Optional.empty());

        int status = restClient().get()
                .uri("/api/v1/airports/XXXX")
                .exchange((req, res) -> res.getStatusCode().value());
        assertThat(status).isEqualTo(404);
    }

    @Test
    void getByIcao_providerFailure_returns503() {
        when(aviationProvider.getByIcao(eq("SBJR")))
                .thenThrow(new RuntimeException("Simulated provider failure (integration test)"));

        int status = restClient().get()
                .uri("/api/v1/airports/SBJR")
                .exchange((req, res) -> res.getStatusCode().value());
        assertThat(status).isEqualTo(503);
    }

    @Test
    void getByIcao_validCode_returns200AndBody() {
        AviationApiAirportDto dto = new AviationApiAirportDto();
        dto.setIcaoIdent("SBSP");
        dto.setFaaIdent("SBSP");
        dto.setFacilityName("Congonhas");
        dto.setCity("São Paulo");
        dto.setState("SP");
        dto.setElevation("802");
        dto.setLatitude("23-37-33.8200S");
        dto.setLongitude("046-39-23.4800W");
        when(aviationProvider.getByIcao(eq("SBSP"))).thenReturn(Optional.of(dto));

        var response = restClient().get()
                .uri("/api/v1/airports/SBSP")
                .retrieve()
                .toEntity(AirportResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().icaoIdent()).isEqualTo("SBSP");
        assertThat(response.getBody().facilityName()).isEqualTo("Congonhas");
    }

    /** Minimal DTO for response assertion (record avoids full AirportResponse dependency). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AirportResponseDto(String icaoIdent, String facilityName) {}
}
