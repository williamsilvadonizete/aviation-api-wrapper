package com.sportygroup.aviation.api.wrapper.service.provider;

import com.sportygroup.aviation.api.wrapper.dto.external.AviationApiAirportDto;

import java.util.Optional;

/**
 * Interface for aviation data provider. Allows swapping the provider (e.g. another site)
 * without impacting the rest of the code—just add a new implementation.
 */
public interface AviationProvider {

    /**
     * Provider name (for logs and fallback).
     */
    String getProviderName();

    /**
     * Fetches airport data by ICAO code.
     */
    Optional<AviationApiAirportDto> getByIcao(String icao);
}
