package com.sportygroup.aviation.api.wrapper.unit;

import com.sportygroup.aviation.api.wrapper.dto.external.AviationApiAirportDto;
import com.sportygroup.aviation.api.wrapper.dto.response.AirportResponse;
import com.sportygroup.aviation.api.wrapper.exception.AirportNotFoundException;
import com.sportygroup.aviation.api.wrapper.mapper.AirportMapper;
import com.sportygroup.aviation.api.wrapper.service.AirportService;
import com.sportygroup.aviation.api.wrapper.service.provider.AviationProvider;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AviationProvider aviationProvider;

    private AirportMapper airportMapper;
    private CircuitBreaker circuitBreaker;
    private Retry retry;
    private AirportService airportService;

    @BeforeEach
    void setUp() {
        airportMapper = new AirportMapper();
        CircuitBreakerRegistry cbRegistry = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom().build());
        circuitBreaker = cbRegistry.circuitBreaker("test");
        RetryRegistry retryRegistry = RetryRegistry.of(RetryConfig.custom().maxAttempts(1).build());
        retry = retryRegistry.retry("test");
        airportService = new AirportService(aviationProvider, airportMapper, circuitBreaker, retry);
    }

    @Test
    void getByIcao_returnsMappedResponse() {
        String icao = "KSPG";
        AviationApiAirportDto dto = new AviationApiAirportDto();
        dto.setIcaoIdent(icao);
        dto.setFaaIdent("SPG");
        dto.setFacilityName("ALBERT WHITTED");
        dto.setCity("ST PETERSBURG");
        dto.setState("FL");
        dto.setStateFull("FLORIDA");
        dto.setElevation("6");
        dto.setLatitude("27-45-54.4100N");
        dto.setLongitude("082-37-37.1060W");

        when(aviationProvider.getByIcao(icao)).thenReturn(Optional.of(dto));

        AirportResponse response = airportService.getByIcao(icao);

        assertThat(response).isNotNull();
        assertThat(response.getIcaoIdent()).isEqualTo(icao);
        assertThat(response.getFaaIdent()).isEqualTo("SPG");
        assertThat(response.getFacilityName()).isEqualTo("ALBERT WHITTED");
        assertThat(response.getCity()).isEqualTo("ST PETERSBURG");
        assertThat(response.getElevation()).isEqualTo("6");
        assertThat(response.getLatitude()).isEqualTo("27-45-54.4100N");
        assertThat(response.getLongitude()).isEqualTo("082-37-37.1060W");
    }

    @Test
    void getByIcao_normalizesToUpperCase() {
        AviationApiAirportDto dto = new AviationApiAirportDto();
        dto.setIcaoIdent("KSPG");
        dto.setFacilityName("Test");
        when(aviationProvider.getByIcao("KSPG")).thenReturn(Optional.of(dto));

        AirportResponse response = airportService.getByIcao("kspg");

        assertThat(response).isNotNull();
        assertThat(response.getIcaoIdent()).isEqualTo("KSPG");
    }

    @Test
    void getByIcao_invalidIcao_throwsIllegalArgument() {
        assertThatThrownBy(() -> airportService.getByIcao("XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ICAO");
    }

    @Test
    void getByIcao_notFound_throwsAirportNotFound() {
        when(aviationProvider.getByIcao("XXXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> airportService.getByIcao("XXXX"))
                .isInstanceOf(AirportNotFoundException.class)
                .hasMessageContaining("XXXX");
    }
}
