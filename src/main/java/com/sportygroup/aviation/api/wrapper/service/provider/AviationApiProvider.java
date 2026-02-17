package com.sportygroup.aviation.api.wrapper.service.provider;

import com.sportygroup.aviation.api.wrapper.client.AviationApiClient;
import com.sportygroup.aviation.api.wrapper.dto.external.AviationApiAirportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provider implementation using aviationapi.com.
 */
@Component
@RequiredArgsConstructor
public class AviationApiProvider implements AviationProvider {

    private final AviationApiClient aviationApiClient;

    @Override
    public String getProviderName() {
        return "AviationApi.com";
    }

    @Override
    public Optional<AviationApiAirportDto> getByIcao(String icao) {
        return aviationApiClient.fetchByIcao(icao);
    }
}
